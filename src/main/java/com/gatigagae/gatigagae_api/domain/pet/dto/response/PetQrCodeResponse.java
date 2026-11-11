package com.gatigagae.gatigagae_api.domain.pet.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "반려견 프로필 입력 QR 코드 응답")
public record PetQrCodeResponse(

	@Schema(description = "QR 코드에 담을 주소", example = "https://api.ditda.kr/profile?code=3f2b8c1e-7d4a-4b9e-9c61-2a5f0e8d7b34")
	String url,

	@Schema(description = "QR 코드를 쓸 수 있는 시간", example = "600")
	long expiresInSeconds

) {
}
