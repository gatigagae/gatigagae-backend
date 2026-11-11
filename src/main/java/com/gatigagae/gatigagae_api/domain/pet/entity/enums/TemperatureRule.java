package com.gatigagae.gatigagae_api.domain.pet.entity.enums;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TemperatureRule {
	BRACHYCEPHALIC(21),    // 단두종
	DOUBLE_COAT(19),       // 이중모
	PUPPY_OR_SENIOR(23),   // 1살 미만 또는 9살 이상
	OBESE(22),             // 비만
	DEFAULT(23);

	private final int temperature;

	public static TemperatureRule of(
		Breed breed,
		CoatType coatType,
		boolean brachycephalic,
		int age,
		BigDecimal weight
	) {
		if (brachycephalic) {
			return BRACHYCEPHALIC;
		}
		if (coatType == CoatType.DOUBLE) {
			return DOUBLE_COAT;
		}
		if (AgeGroup.of(age) != AgeGroup.ADULT) {
			return PUPPY_OR_SENIOR;
		}
		if (breed.isObese(weight)) {
			return OBESE;
		}
		return DEFAULT;
	}
}
