package com.scaneat.back.dto.scanlog;

import com.scaneat.back.entity.UsrScanLog;
import java.time.LocalDateTime;

public record ScanLogResponse(
		Long id,
		String uuid,
		String bizRegNo,
		LocalDateTime scanDt
) {
	public static ScanLogResponse from(UsrScanLog log) {
		return new ScanLogResponse(log.getId(), log.getUuid(), log.getBizRegNo(), log.getScanDt());
	}
}
