package com.scaneat.back.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BizPgId implements Serializable {

	@Column(name = "biz_reg_no", length = 20)
	private String bizRegNo;

	@Column(name = "pg_provider", length = 20)
	private String pgProvider;
}
