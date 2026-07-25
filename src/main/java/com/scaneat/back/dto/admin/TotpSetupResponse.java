package com.scaneat.back.dto.admin;

public record TotpSetupResponse(String secret, String otpauthUri) {
}
