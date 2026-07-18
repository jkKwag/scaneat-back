package com.scaneat.back.dto.consent;

import com.scaneat.back.entity.UsrPrvCns;
import java.time.LocalDateTime;

public record ConsentResponse(
		Long id,
		String uuid,
		String bizRegNo,
		String guestName,
		String guestPhone,
		LocalDateTime consentAt
) {
	public static ConsentResponse from(UsrPrvCns consent) {
		return new ConsentResponse(
				consent.getId(),
				consent.getUuid(),
				consent.getBizRegNo(),
				consent.getGuestName(),
				consent.getGuestPhone(),
				consent.getConsentAt()
		);
	}
}
