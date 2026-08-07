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

	// nullable=false로 두면 기존 행이 있는 테이블에 기본값 없이 NOT NULL 컬럼을 추가하는 ALTER를 Postgres가 거부해서
	// ddl-auto:update가 이 컬럼을 계속 못 만든다 — nullable로 둬야 다음 배포 때 정상적으로 컬럼이 생성된다.
	@Column(name = "biz_reg_no", length = 20)
	private String bizRegNo;

	@Column(name = "is_public", nullable = false)
	private Boolean isPublic;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "answered_at")
	private LocalDateTime answeredAt;
}
