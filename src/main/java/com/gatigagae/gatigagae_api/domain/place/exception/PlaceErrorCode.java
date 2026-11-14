package com.gatigagae.gatigagae_api.domain.place.exception;

import org.springframework.http.HttpStatus;

import com.gatigagae.gatigagae_api.global.apipayload.code.BaseErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PlaceErrorCode implements BaseErrorCode {

	PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, "PLACE_404_01", "장소를 찾을 수 없습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
