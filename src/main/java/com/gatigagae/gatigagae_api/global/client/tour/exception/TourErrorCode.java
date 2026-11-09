package com.gatigagae.gatigagae_api.global.client.tour.exception;

import org.springframework.http.HttpStatus;

import com.gatigagae.gatigagae_api.global.apipayload.code.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TourErrorCode implements BaseErrorCode {

	TOUR_API_FAILED(HttpStatus.BAD_GATEWAY, "TOUR_502_01", "관광공사 API 호출에 실패했습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
