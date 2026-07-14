package com.scaneat.back.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_usr_chat_msg")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsrChatMsg {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "rsvn_no", length = 30, nullable = false)
	private String rsvnNo;

	@Enumerated(EnumType.STRING)
	@Column(name = "sender", length = 20, nullable = false)
	private ChatSender sender;

	@Column(name = "message", length = 2000, nullable = false)
	private String message;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;
}
