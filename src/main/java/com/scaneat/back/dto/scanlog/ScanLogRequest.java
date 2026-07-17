package com.scaneat.back.dto.scanlog;

import jakarta.validation.constraints.NotBlank;

public record ScanLogRequest(
		@NotBlank(message = "uuid는 필수입니다.") String uuid,
		@NotBlank(message = "bizRegNo는 필수입니다.") String bizRegNo,
		String vstTypCd
) {
}
