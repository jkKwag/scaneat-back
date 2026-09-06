package com.scaneat.back.dto.admin;

import com.scaneat.back.entity.AdminPasskey;
import java.time.LocalDateTime;

public record PasskeyDeviceResponse(
		String credId,
		String deviceLabel,
		String platform,
		LocalDateTime regDt,
		LocalDateTime lastUsedDt
) {
	public static PasskeyDeviceResponse from(AdminPasskey passkey) {
		return new PasskeyDeviceResponse(
				passkey.getCredId(), passkey.getDeviceLabel(), passkey.getPlatform(),
				passkey.getRegDt(), passkey.getLastUsedDt());
	}
}
