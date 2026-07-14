package com.scaneat.back.service;

import com.scaneat.back.dto.scanlog.ScanLogRequest;
import com.scaneat.back.dto.scanlog.ScanLogResponse;
import com.scaneat.back.entity.UsrScanLog;
import com.scaneat.back.repository.UsrScanLogRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScanLogService {

	private final UsrScanLogRepository usrScanLogRepository;

	public List<ScanLogResponse> getScanLogs(String uuid) {
		return usrScanLogRepository.findByUuidOrderByScanDtDesc(uuid).stream()
				.map(ScanLogResponse::from)
				.toList();
	}

	@Transactional
	public ScanLogResponse createScanLog(ScanLogRequest request) {
		UsrScanLog log = UsrScanLog.builder()
				.uuid(request.uuid())
				.bizRegNo(request.bizRegNo())
				.scanDt(LocalDateTime.now())
				.build();
		return ScanLogResponse.from(usrScanLogRepository.save(log));
	}
}
