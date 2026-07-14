package com.scaneat.back.dto.consent;

import com.scaneat.back.entity.UsrPrvCns;
import java.time.LocalDateTime;

public record ConsentResponse(
		Long id,
		String uuid,
		String bizRegNo,
		String guestName,
		LocalDateTime cnsDt
) {
	public static ConsentResponse from(UsrPrvCns consent) {
		return new ConsentResponse(
				consent.getId(),
				consent.getUuid(),
				consent.getBizRegNo(),
				consent.getGuestName(),
				consent.getCnsDt()
		);
	}
}
