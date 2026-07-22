package com.scaneat.back.service;

import com.scaneat.back.dto.dashboard.BizRevenueRankResponse;
import com.scaneat.back.dto.dashboard.DashboardCodeCount;
import com.scaneat.back.dto.dashboard.DashboardOverviewResponse;
import com.scaneat.back.dto.dashboard.SecurityMonitorResponse;
import com.scaneat.back.entity.AdminRole;
import com.scaneat.back.entity.Biz;
import com.scaneat.back.repository.AdminUsrRepository;
import com.scaneat.back.repository.BizEmpRepository;
import com.scaneat.back.repository.BizRepository;
import com.scaneat.back.repository.UsrPaymentRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

	private final BizRepository bizRepository;
	private final UsrPaymentRepository usrPaymentRepository;
	private final AdminUsrRepository adminUsrRepository;
	private final BizEmpRepository bizEmpRepository;

	public DashboardOverviewResponse getOverview() {
		List<Biz> all = bizRepository.findAll();

		Map<String, Long> byStatus = all.stream()
				.collect(Collectors.groupingBy(b -> b.getBizStatus() == null ? "UNKNOWN" : b.getBizStatus(), Collectors.counting()));
		Map<String, Long> byIndustry = all.stream()
				.filter(b -> b.getIndCd() != null)
				.collect(Collectors.groupingBy(Biz::getIndCd, Collectors.counting()));

		return new DashboardOverviewResponse(all.size(), toSortedCounts(byStatus), toSortedCounts(byIndustry));
	}

	public List<BizRevenueRankResponse> getRevenueRanking(int limit) {
		return usrPaymentRepository.findTopBizByRevenue(limit).stream()
				.map(p -> new BizRevenueRankResponse(p.getBizRegNo(), p.getBizNm(), p.getTotalAmount(), p.getPaymentCount()))
				.toList();
	}

	public SecurityMonitorResponse getSecurityMonitor() {
		long totalAdmin = adminUsrRepository.count();
		long superAdmin = adminUsrRepository.countByAdminRole(AdminRole.SUPER);
		long bizAdmin = adminUsrRepository.countByAdminRole(AdminRole.BIZ);
		long inactiveAdmin = adminUsrRepository.countByUseYn("N");
		long totalEmployee = bizEmpRepository.count();
		long activeEmployee = bizEmpRepository.countByUseYn("Y");
		long inactiveEmployee = bizEmpRepository.countByUseYn("N");
		return new SecurityMonitorResponse(totalAdmin, superAdmin, bizAdmin, inactiveAdmin, totalEmployee, activeEmployee, inactiveEmployee);
	}

	private List<DashboardCodeCount> toSortedCounts(Map<String, Long> counts) {
		return counts.entrySet().stream()
				.map(e -> new DashboardCodeCount(e.getKey(), e.getValue()))
				.sorted(Comparator.comparingLong(DashboardCodeCount::count).reversed())
				.toList();
	}
}
