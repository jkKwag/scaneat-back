package com.scaneat.back.dto.qna;

import com.scaneat.back.entity.Qna;
import java.time.LocalDateTime;

public record QnaResponse(
		Long id,
		String question,
		String answer,
		String bizRegNo,
		Boolean isPublic,
		LocalDateTime createdAt,
		LocalDateTime answeredAt
) {
	public static QnaResponse from(Qna qna) {
		return new QnaResponse(
				qna.getId(),
				qna.getQuestion(),
				qna.getAnswer(),
				qna.getBizRegNo(),
				qna.getIsPublic(),
				qna.getCreatedAt(),
				qna.getAnsweredAt()
		);
	}
}
