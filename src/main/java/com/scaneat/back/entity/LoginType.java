package com.scaneat.back.entity;

// 계정이 어떤 방식으로 가입/로그인하는지. tb_admin_oauth와 함께 쓰인다 —
// EMAIL 이외의 값이면 password_hash는 null이고 tb_admin_oauth에 연동 정보가 있다.
public enum LoginType {
	EMAIL,
	KAKAO
}
