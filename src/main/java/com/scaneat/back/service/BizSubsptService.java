package com.scaneat.back.service;

import com.scaneat.back.client.TossPaymentsClient;
import com.scaneat.back.common.exception.BusinessException;
import com.scaneat.back.common.exception.ResourceNotFoundException;
import com.scaneat.back.dto.subspt.BizSubPlanResponse;
import com.scaneat.back.dto.subspt.BizSubsptPaymentResponse;
import com.scaneat.back.dto.subspt.BizSubsptResponse;
import com.scaneat.back.dto.subspt.BizSubsptStartRequest;
import com.scaneat.back.entity.AdminRole;
import com.scaneat.back.entity.BizSubPlan;
import com.scaneat.back.entity.BizSubspt;
import com.scaneat.back.entity.BizSubsptPayment;
import com.scaneat.back.repository.AdminUsrRepository;
import com.scaneat.back.repository.BizSubPlanRepository;
import com.scaneat.back.repository.BizSubsptPaymentRepository;
import com.scaneat.back.repository.BizSubsptRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BizSubsptService {

	private static final DateTimeFormatter BILLING_PERIOD_FMT = DateTimeFormatter.ofPattern("yyyy-MM");

	private final TossPaymentsClient tossPaymentsClient;
	private final BizSubPlanRepository bizSubPlanRepository;
	private final BizSubsptRepository bizSubsptRepository;
	private final BizSubsptPaymentRepository bizSubsptPaymentRepository;
	private final AdminUsrRepository adminUsrRepository;
	private final BizService bizService;

	public List<BizSubPlanResponse> getPlans() {
		return bizSubPlanRepository.findByUseYnOrderBySortOrdAsc("Y").stream()
				.map(BizSubPlanResponse::from)
				.toList();
	}

	// 구독 시작 전이면 null — "아직 구독 안 함"은 에러가 아니라 정상 상태라 404를 던지지 않는다.
	public BizSubsptResponse getSubscription(String bizRegNo) {
		return bizSubsptRepository.findById(bizRegNo)
				.map(this::toResponse)
				.orElse(null);
	}

	public List<BizSubsptPaymentResponse> getPayments(String bizRegNo) {
		return bizSubsptPaymentRepository.findByBizRegNoOrderByRegDtDesc(bizRegNo).stream()
				.map(BizSubsptPaymentResponse::from)
				.toList();
	}

	// 카드 등록(빌링 인증) 위젯 완료 → billingKey 발급 → 첫 달 구독료 즉시 청구 → 구독 현황/결제내역 저장.
	// 최초 가입 또는 해지 후 재가입 전용. 이미 ACTIVE로 구독 중일 때 요금제만 바꾸는 경우는
	// schedulePlanChange()로 처리한다 (일할 정산/환불 없이 다음 결제일부터 새 요금제 적용).
	@Transactional
	public BizSubsptResponse startSubscription(String bizRegNo, BizSubsptStartRequest request, String actorId) {
		BizSubPlan plan = bizSubPlanRepository.findById(request.planCd())
				.orElseThrow(() -> new ResourceNotFoundException("요금제를 찾을 수 없습니다: " + request.planCd()));

		BizSubspt existingSubspt = bizSubsptRepository.findById(bizRegNo).orElse(null);
		if (existingSubspt != null && "ACTIVE".equals(existingSubspt.getStatus())) {
			throw new BusinessException("이미 구독 중입니다. 요금제 변경은 요금제 변경 기능을 이용해주세요.");
		}

		List<String> missingBizFields = bizService.getMissingBizInfoFields(bizRegNo);
		if (!missingBizFields.isEmpty()) {
			throw new BusinessException("사업장 정보 메뉴에서 저장 후 가능합니다 (" + String.join(", ", missingBizFields) + ")");
		}

		Map<String, Object> billingAuth = tossPaymentsClient.issueBillingKey(request.authKey(), request.customerKey());
		log.info("[Toss] billing key issue raw response: {}", billingAuth);
		String billingKey = (String) billingAuth.get("billingKey");
		if (billingKey == null || billingKey.isBlank()) {
			throw new BusinessException("빌링키 발급에 실패했습니다.");
		}

		LocalDateTime now = LocalDateTime.now();
		LocalDate today = now.toLocalDate();
		BigDecimal totalAmount = plan.getSuppliedAmount().add(plan.getVat());
		String billingPeriod = today.format(BILLING_PERIOD_FMT);
		String orderId = "subspt-" + bizRegNo + "-" + billingPeriod + "-" + System.currentTimeMillis();

		Map<String, Object> chargeResult = tossPaymentsClient.chargeBilling(
				billingKey, request.customerKey(), orderId, plan.getPlanNm() + " 구독료", totalAmount);
		log.info("[Toss] billing charge raw response: {}", chargeResult);

		String status = (String) chargeResult.get("status");
		BizSubsptPayment payment = BizSubsptPayment.builder()
				.paymentKey((String) chargeResult.get("paymentKey"))
				.bizRegNo(bizRegNo)
				.planCd(plan.getPlanCd())
				.billingKey(billingKey)
				.billingPeriod(billingPeriod)
				.suppliedAmount(toBigDecimal(chargeResult.get("suppliedAmount")))
				.vat(toBigDecimal(chargeResult.get("vat")))
				.status(status)
				.requestedDt(parseTossDateTime((String) chargeResult.get("requestedAt")))
				.approvedDt(parseTossDateTime((String) chargeResult.get("approvedAt")))
				.receiptUrl(extractReceiptUrl(chargeResult))
				.regUsrId(actorId)
				.regDt(now)
				.build();
		bizSubsptPaymentRepository.save(payment);

		if (!"DONE".equals(status)) {
			throw new BusinessException(HttpStatus.BAD_GATEWAY, "첫 구독료 결제에 실패했습니다: " + status);
		}

		BizSubspt subspt = existingSubspt;
		LocalDate nextBillingDt = today.plusMonths(1);
		if (subspt == null) {
			subspt = BizSubspt.builder()
					.bizRegNo(bizRegNo)
					.planCd(plan.getPlanCd())
					.suppliedAmount(plan.getSuppliedAmount())
					.vat(plan.getVat())
					.billingKey(billingKey)
					.billingDay(today.getDayOfMonth())
					.nextBillingDt(nextBillingDt)
					.status("ACTIVE")
					.startedDt(now)
					.regUsrId(actorId)
					.regDt(now)
					.build();
		} else {
			subspt.setPlanCd(plan.getPlanCd());
			subspt.setSuppliedAmount(plan.getSuppliedAmount());
			subspt.setVat(plan.getVat());
			subspt.setBillingKey(billingKey);
			subspt.setNextBillingDt(nextBillingDt);
			subspt.setStatus("ACTIVE");
			subspt.setPendingPlanCd(null);
			subspt.setCanceledDt(null);
			subspt.setUpdUsrId(actorId);
			subspt.setUpdDt(now);
		}
		bizSubsptRepository.save(subspt);
		promoteProvAdmins(bizRegNo);
		if (existingSubspt == null) {
			bizService.seedFromTemplate(bizRegNo);
		}

		return toResponse(subspt);
	}

	// 이미 ACTIVE인 구독의 요금제만 바꿀 때 쓴다. 일할 정산/환불 없이 예약만 해두고, 다음 결제일 배치에서
	// pendingPlanCd로 plan_cd를 갈아끼우면서 그 금액으로 정상 청구한다. 현재 요금제로 되돌리면(=예약 취소) pendingPlanCd를 비운다.
	@Transactional
	public BizSubsptResponse schedulePlanChange(String bizRegNo, String planCd, String actorId) {
		BizSubspt subspt = bizSubsptRepository.findById(bizRegNo)
				.filter(s -> "ACTIVE".equals(s.getStatus()))
				.orElseThrow(() -> new BusinessException("현재 이용 중인 구독이 없습니다."));

		BizSubPlan plan = bizSubPlanRepository.findById(planCd)
				.orElseThrow(() -> new ResourceNotFoundException("요금제를 찾을 수 없습니다: " + planCd));

		subspt.setPendingPlanCd(plan.getPlanCd().equals(subspt.getPlanCd()) ? null : plan.getPlanCd());
		subspt.setUpdUsrId(actorId);
		subspt.setUpdDt(LocalDateTime.now());
		bizSubsptRepository.save(subspt);

		return toResponse(subspt);
	}

	// 구독료 결제가 처음 성공하면(가입 후 승인 대기 상태였더라도) 그 사업장 관리자 계정을 정식 BIZ 권한으로 승격한다.
	// SUPER의 수동 승인(approveBiz)과 별개 경로 — 둘 중 먼저 되는 쪽에서 승격되면 된다.
	private void promoteProvAdmins(String bizRegNo) {
		adminUsrRepository.findByBizRegNoOrderByRegDtAsc(bizRegNo).forEach(admin -> {
			if (admin.getAdminRole() == AdminRole.PROV_ADMIN) {
				admin.setAdminRole(AdminRole.BIZ);
				adminUsrRepository.save(admin);
			}
		});
	}

	@Transactional
	public void cancelSubscription(String bizRegNo, String actorId) {
		BizSubspt subspt = bizSubsptRepository.findById(bizRegNo)
				.orElseThrow(() -> new ResourceNotFoundException("구독 정보를 찾을 수 없습니다: " + bizRegNo));
		LocalDateTime now = LocalDateTime.now();
		subspt.setStatus("CANCELED");
		subspt.setCanceledDt(now);
		subspt.setPendingPlanCd(null);
		subspt.setUpdUsrId(actorId);
		subspt.setUpdDt(now);
		bizSubsptRepository.save(subspt);
	}

	private BizSubsptResponse toResponse(BizSubspt subspt) {
		String pendingPlanNm = subspt.getPendingPlanCd() == null ? null : planNameOf(subspt.getPendingPlanCd());
		return BizSubsptResponse.from(subspt, planNameOf(subspt.getPlanCd()), pendingPlanNm);
	}

	private String planNameOf(String planCd) {
		return bizSubPlanRepository.findById(planCd).map(BizSubPlan::getPlanNm).orElse(planCd);
	}

	private String extractReceiptUrl(Map<String, Object> tossResult) {
		Object receiptObj = tossResult.get("receipt");
		return receiptObj instanceof Map<?, ?> receipt ? (String) receipt.get("url") : null;
	}

	private LocalDateTime parseTossDateTime(String raw) {
		if (raw == null || raw.isBlank()) {
			return null;
		}
		try {
			return OffsetDateTime.parse(raw).toLocalDateTime();
		} catch (DateTimeParseException ex) {
			throw new BusinessException("결제 일시 형식을 해석할 수 없습니다: " + raw);
		}
	}

	private BigDecimal toBigDecimal(Object value) {
		return value instanceof Number number ? new BigDecimal(number.toString()) : null;
	}
}
