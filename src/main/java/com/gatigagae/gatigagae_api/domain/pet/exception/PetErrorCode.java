package com.gatigagae.gatigagae_api.domain.pet.exception;

import org.springframework.http.HttpStatus;

import com.gatigagae.gatigagae_api.global.apipayload.code.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PetErrorCode implements BaseErrorCode {

	INVALID_IMAGE_TYPE(HttpStatus.BAD_REQUEST, "PET_400_01", "사진은 jpg, png, webp 파일만 올릴 수 있습니다."),

	INVALID_QR_CODE(HttpStatus.UNAUTHORIZED, "PET_401_01", "만료됐거나 이미 사용한 QR 코드입니다. 차량에서 QR 코드를 다시 띄워 주세요."),

	PET_NOT_FOUND(HttpStatus.NOT_FOUND, "PET_404_01", "등록된 반려견이 없습니다."),
	QR_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "PET_404_02", "발급한 QR 코드가 없습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
