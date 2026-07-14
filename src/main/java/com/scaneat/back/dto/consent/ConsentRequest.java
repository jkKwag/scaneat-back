package com.scaneat.back.dto.consent;

import jakarta.validation.constraints.NotBlank;

public record ConsentRequest(
		@NotBlank(message = "uuid는 필수입니다.") String uuid,
		@NotBlank(message = "bizRegNo는 필수입니다.") String bizRegNo,
		String guestName
) {
}
