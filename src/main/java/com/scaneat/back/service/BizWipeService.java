package com.scaneat.back.service;

import com.scaneat.back.common.exception.BusinessException;
import com.scaneat.back.common.exception.ResourceNotFoundException;
import com.scaneat.back.common.security.CurrentAdmin;
import com.scaneat.back.dto.biz.BizWipeResponse;
import com.scaneat.back.entity.AdminUsr;
import com.scaneat.back.entity.BizMenu;
import com.scaneat.back.entity.BizMenuOptCd;
import com.scaneat.back.entity.UsrOrder;
import com.scaneat.back.entity.UsrPayment;
import com.scaneat.back.entity.UsrRsvn;
import com.scaneat.back.repository.AdminSessionRepository;
import com.scaneat.back.repository.AdminUsrRepository;
import com.scaneat.back.repository.BizCatRepository;
import com.scaneat.back.repository.BizEmpRepository;
import com.scaneat.back.repository.BizHourStdRepository;
import com.scaneat.back.repository.BizMenuOptCdRepository;
import com.scaneat.back.repository.BizMenuOptGrpRepository;
import com.scaneat.back.repository.BizMenuRepository;
import com.scaneat.back.repository.BizRepository;
import com.scaneat.back.repository.BizRsvnStdRepository;
import com.scaneat.back.repository.BizSeatRepository;
import com.scaneat.back.repository.BizSubsptPaymentRepository;
import com.scaneat.back.repository.BizSubsptRepository;
import com.scaneat.back.repository.EmailVerifyCodeRepository;
import com.scaneat.back.repository.UsrChatMsgRepository;
import com.scaneat.back.repository.UsrOrderItemOptRepository;
import com.scaneat.back.repository.UsrOrderItemRepository;
import com.scaneat.back.repository.UsrOrderRepository;
import com.scaneat.back.repository.UsrPaymentOrderRepository;
import com.scaneat.back.repository.UsrPaymentPgRepository;
import com.scaneat.back.repository.UsrPaymentRepository;
import com.scaneat.back.repository.UsrPrvCnsRepository;
import com.scaneat.back.repository.UsrRsvnRepository;
import com.scaneat.back.repository.UsrScanLogRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 테스트 목적으로 특정 사업자의 모든 데이터(계정/메뉴/주문/결제/구독 등)를 통째로 지우는 전용 서비스.
// 실서비스 흐름과는 무관하고, "가입 → 구독료 결제"를 같은 사업자번호로 반복 테스트할 때만 쓴다.
@Service
@RequiredArgsConstructor
public class BizWipeService {

	private final BizRepository bizRepository;
	private final AdminUsrRepository adminUsrRepository;
	private final AdminSessionRepository adminSessionRepository;
	private final EmailVerifyCodeRepository emailVerifyCodeRepository;
	private final BizCatRepository bizCatRepository;
	private final BizMenuRepository bizMenuRepository;
	private final BizMenuOptGrpRepository bizMenuOptGrpRepository;
	private final BizMenuOptCdRepository bizMenuOptCdRepository;
	private final BizSeatRepository bizSeatRepository;
	private final BizHourStdRepository bizHourStdRepository;
	private final BizRsvnStdRepository bizRsvnStdRepository;
	private final BizEmpRepository bizEmpRepository;
	private final BizSubsptRepository bizSubsptRepository;
	private final BizSubsptPaymentRepository bizSubsptPaymentRepository;
	private final UsrOrderRepository usrOrderRepository;
	private final UsrOrderItemRepository usrOrderItemRepository;
	private final UsrOrderItemOptRepository usrOrderItemOptRepository;
	private final UsrPaymentRepository usrPaymentRepository;
	private final UsrPaymentOrderRepository usrPaymentOrderRepository;
	private final UsrPaymentPgRepository usrPaymentPgRepository;
	private final UsrRsvnRepository usrRsvnRepository;
	private final UsrChatMsgRepository usrChatMsgRepository;
	private final UsrScanLogRepository usrScanLogRepository;
	private final UsrPrvCnsRepository usrPrvCnsRepository;

	@Transactional
	public BizWipeResponse wipeAllData(String bizRegNo, CurrentAdmin requester) {
		if (!requester.isSuper() && !bizRegNo.equals(requester.bizRegNo())) {
			throw new BusinessException(HttpStatus.FORBIDDEN, "본인 사업장이거나 슈퍼관리자만 처리할 수 있습니다.");
		}
		if (!bizRepository.existsById(bizRegNo)) {
			throw new ResourceNotFoundException("사업자를 찾을 수 없습니다: " + bizRegNo);
		}

		List<UsrOrder> orders = usrOrderRepository.findByBizRegNoOrderByRegDtDesc(bizRegNo);
		List<UsrPayment> payments = usrPaymentRepository.findByBizRegNoOrderByRegDtDesc(bizRegNo);
		List<UsrRsvn> rsvns = usrRsvnRepository.findByBizRegNoOrderByRsvnDtDesc(bizRegNo);
		List<BizMenu> menus = bizMenuRepository.findByBizRegNoOrderBySortOrdAsc(bizRegNo);
		List<AdminUsr> admins = adminUsrRepository.findByBizRegNoOrderByRegDtAsc(bizRegNo);

		// tier 1: 주문 품목/옵션, 결제-주문 연결, 예약 채팅
		for (UsrOrder order : orders) {
			usrOrderItemOptRepository.deleteAll(usrOrderItemOptRepository.findById_OrderNoOrderById_OrderSeqAsc(order.getOrderNo()));
			usrOrderItemRepository.deleteAll(usrOrderItemRepository.findById_OrderNoOrderById_OrderSeqAsc(order.getOrderNo()));
		}
		for (UsrPayment payment : payments) {
			usrPaymentOrderRepository.deleteAll(usrPaymentOrderRepository.findById_PaymentKey(payment.getPaymentKey()));
		}
		usrPaymentPgRepository.deleteAll(usrPaymentPgRepository.findAllById(payments.stream().map(UsrPayment::getPaymentKey).toList()));
		for (UsrRsvn rsvn : rsvns) {
			usrChatMsgRepository.deleteAll(usrChatMsgRepository.findByRsvnNoOrderByCreatedAtAsc(rsvn.getRsvnNo()));
		}

		// tier 2: 메뉴 옵션 — 옵션그룹은 메뉴 전용 개념이라 옵션 삭제 후 남은 그룹코드를 모아서 같이 지운다.
		Set<String> optGrpCds = new HashSet<>();
		for (BizMenu menu : menus) {
			List<BizMenuOptCd> optCds = bizMenuOptCdRepository.findByMenuCd(menu.getMenuCd());
			optCds.forEach(o -> optGrpCds.add(o.getOptGrpCd()));
			bizMenuOptCdRepository.deleteAll(optCds);
		}
		bizMenuOptGrpRepository.deleteAll(bizMenuOptGrpRepository.findAllById(optGrpCds));

		// tier 3: 주문/결제/예약/메뉴 본체
		usrOrderRepository.deleteAll(orders);
		usrPaymentRepository.deleteAll(payments);
		usrRsvnRepository.deleteAll(rsvns);
		bizMenuRepository.deleteAll(menus);

		// tier 4: 카테고리/좌석/영업시간/예약기준/직원/구독결제내역/방문로그/동의이력/세션
		// (QnA는 삭제 대상에서 제외 — 별도로 정리)
		bizCatRepository.deleteAll(bizCatRepository.findByBizRegNoOrderBySortOrdAsc(bizRegNo));
		bizSeatRepository.deleteAll(bizSeatRepository.findById_BizRegNoOrderBySortOrdAsc(bizRegNo));
		bizHourStdRepository.deleteAll(bizHourStdRepository.findById_BizRegNo(bizRegNo));
		bizRsvnStdRepository.findById(bizRegNo).ifPresent(bizRsvnStdRepository::delete);
		bizEmpRepository.deleteAll(bizEmpRepository.findByBizRegNoOrderByRegDtAsc(bizRegNo));
		bizSubsptPaymentRepository.deleteAll(bizSubsptPaymentRepository.findByBizRegNoOrderByRegDtDesc(bizRegNo));
		usrScanLogRepository.deleteAll(usrScanLogRepository.findByBizRegNoOrderByVstDtDesc(bizRegNo));
		usrPrvCnsRepository.deleteAll(usrPrvCnsRepository.findByBizRegNo(bizRegNo));
		adminSessionRepository.deleteAll(adminSessionRepository.findByBizRegNo(bizRegNo));

		// tier 5: 구독 상태 + 관리자 계정(+ 이메일 인증코드)
		bizSubsptRepository.findById(bizRegNo).ifPresent(bizSubsptRepository::delete);
		for (AdminUsr admin : admins) {
			emailVerifyCodeRepository.findById(admin.getAdminId()).ifPresent(emailVerifyCodeRepository::delete);
		}
		adminUsrRepository.deleteAll(admins);

		// tier 6: 사업자 본체
		bizRepository.deleteById(bizRegNo);

		return new BizWipeResponse(admins.size(), menus.size(), orders.size(), payments.size(), rsvns.size());
	}
}
