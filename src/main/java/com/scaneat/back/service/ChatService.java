package com.scaneat.back.service;

import com.scaneat.back.dto.chat.ChatMessageRequest;
import com.scaneat.back.dto.chat.ChatMessageResponse;
import com.scaneat.back.entity.ChatSender;
import com.scaneat.back.entity.UsrChatMsg;
import com.scaneat.back.repository.UsrChatMsgRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

	private final UsrChatMsgRepository usrChatMsgRepository;

	public List<ChatMessageResponse> getMessages(String rsvnNo) {
		return usrChatMsgRepository.findByRsvnNoOrderByCreatedAtAsc(rsvnNo).stream()
				.map(ChatMessageResponse::from)
				.toList();
	}

	@Transactional
	public ChatMessageResponse createMessage(String rsvnNo, ChatMessageRequest request) {
		UsrChatMsg message = UsrChatMsg.builder()
				.rsvnNo(rsvnNo)
				.sender(ChatSender.valueOf(request.sender().toUpperCase()))
				.message(request.message())
				.createdAt(LocalDateTime.now())
				.build();
		return ChatMessageResponse.from(usrChatMsgRepository.save(message));
	}
}
