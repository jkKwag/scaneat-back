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
public class AdminOauthId implements Serializable {

	@Column(name = "admin_no", length = 36)
	private String adminNo;

	// tb_admin_usr.admin_no 또는 tb_biz_emp.emp_no를 가리키는지 구분 — AdminRole.name()
	// 또는 "EMPLOYEE" 값을 그대로 쓴다 (세션 테이블의 admin_role 관례와 동일).
	@Column(name = "admin_type", length = 20)
	private String adminType;

	@Column(name = "provider", length = 20)
	private String provider;
}
