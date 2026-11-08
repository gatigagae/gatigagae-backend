package com.gatigagae.gatigagae_api.global.apipayload.code;

import org.springframework.http.HttpStatus;

public interface BaseErrorCode {
	HttpStatus getHttpStatus();

	String getCode();

	String getMessage();
}
