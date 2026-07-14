package com.scaneat.back.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "supporters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Supporter {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", length = 50, nullable = false)
	private String name;

	@Column(name = "amount", precision = 12, scale = 0, nullable = false)
	private BigDecimal amount;

	@Column(name = "message", length = 500)
	private String message;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;
}
