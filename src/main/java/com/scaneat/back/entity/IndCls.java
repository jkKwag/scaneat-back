package com.scaneat.back.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_ind_cls")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndCls {

	@Id
	@Column(name = "ind_cd", length = 20)
	private String indCd;

	@Column(name = "ind_nm", length = 100, nullable = false)
	private String indNm;
}
