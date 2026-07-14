package com.scaneat.back.common.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BusinessException {

	public ResourceNotFoundException(String message) {
		super(HttpStatus.NOT_FOUND, message);
	}
}
