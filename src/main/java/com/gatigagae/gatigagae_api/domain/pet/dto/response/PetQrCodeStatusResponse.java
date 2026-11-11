package com.gatigagae.gatigagae_api.domain.pet.dto.response;

import com.gatigagae.gatigagae_api.domain.pet.entity.enums.PetQrCodeStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "QR 코드 입력 상태 응답")
public record PetQrCodeStatusResponse(

	@Schema(description = "`WAITING`: 아직 저장 안 함, `COMPLETED`: 저장함, `EXPIRED`: 저장하지 않고 시간이 지남", example = "WAITING")
	PetQrCodeStatus status

) {
}
