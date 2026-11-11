package com.gatigagae.gatigagae_api.domain.pet.dto.request;

import java.math.BigDecimal;

import com.gatigagae.gatigagae_api.domain.pet.entity.enums.Breed;
import com.gatigagae.gatigagae_api.domain.pet.entity.enums.CoatType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "맞춤 실내 온도 미리보기 요청")
public record TemperatureRequest(

	@Schema(description = "견종", example = "MALTESE")
	@NotNull(message = "견종은 필수입니다.")
	Breed breed,

	@Schema(description = "털 유형. `DOUBLE`: 이중모, `SINGLE`: 단일모", example = "SINGLE")
	@NotNull(message = "털 유형은 필수입니다.")
	CoatType coatType,

	@Schema(description = "단두종 여부", example = "false")
	@NotNull(message = "단두종 여부는 필수입니다.")
	Boolean brachycephalic,

	@Schema(description = "나이", example = "4")
	@NotNull(message = "나이는 필수입니다.")
	@Min(value = 0, message = "나이는 0살 이상이어야 합니다.")
	@Max(value = 30, message = "나이는 30살 이하여야 합니다.")
	Integer age,

	@Schema(description = "몸무게", example = "4.2")
	@DecimalMin(value = "0", inclusive = false, message = "몸무게는 0보다 커야 합니다.")
	@Digits(integer = 3, fraction = 2, message = "몸무게는 999.99kg 이하, 소수점 둘째 자리까지 입력할 수 있습니다.")
	BigDecimal weight

) {
}
