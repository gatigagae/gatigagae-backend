package com.gatigagae.gatigagae_api.domain.pet.dto.response;

import java.math.BigDecimal;

import com.gatigagae.gatigagae_api.domain.pet.entity.enums.Breed;
import com.gatigagae.gatigagae_api.domain.pet.entity.enums.CoatType;
import com.gatigagae.gatigagae_api.domain.pet.entity.enums.TemperatureRule;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "맞춤 실내 온도 응답")
public record TemperatureResponse(

	@Schema(description = "맞춤 실내 온도", example = "23")
	int temperature

) {
	public static TemperatureResponse of(
		Breed breed,
		CoatType coatType,
		boolean brachycephalic,
		int age,
		BigDecimal weight
	) {
		TemperatureRule rule = TemperatureRule.of(breed, coatType, brachycephalic, age, weight);
		
		return new TemperatureResponse(rule.getTemperature());
	}
}
