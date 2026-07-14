package com.scaneat.back.dto.chat;

import com.scaneat.back.entity.UsrChatMsg;
import java.time.LocalDateTime;

public record ChatMessageResponse(
		Long id,
		String rsvnNo,
		String sender,
		String message,
		LocalDateTime createdAt
) {
	public static ChatMessageResponse from(UsrChatMsg msg) {
		return new ChatMessageResponse(
				msg.getId(),
				msg.getRsvnNo(),
				msg.getSender().name(),
				msg.getMessage(),
				msg.getCreatedAt()
		);
	}
}
