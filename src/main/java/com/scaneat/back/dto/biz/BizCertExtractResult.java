package com.scaneat.back.dto.biz;

// 사업자등록증 이미지에서 Gemini가 읽어낸 값 — 읽지 못한 항목은 null.
public record BizCertExtractResult(String bizNm, String repNm, String addr, String bizRegNo) {
}
