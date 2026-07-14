package com.scaneat.back.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "qna")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Qna {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "question", length = 2000, nullable = false)
	private String question;

	@Column(name = "answer", length = 2000)
	private String answer;

	@Column(name = "biz_reg_no", length = 20, nullable = false)
	private String bizRegNo;

	@Column(name = "is_public", nullable = false)
	private Boolean isPublic;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "answered_at")
	private LocalDateTime answeredAt;
}
