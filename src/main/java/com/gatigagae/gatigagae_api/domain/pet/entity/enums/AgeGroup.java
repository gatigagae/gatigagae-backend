package com.gatigagae.gatigagae_api.domain.pet.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AgeGroup {
	PUPPY("퍼피"),    // 1살 미만
	ADULT("성견"),    // 1 ~ 8살
	SENIOR("노견");   // 9살 이상

	private final String displayName;

	public static AgeGroup of(int age) {
		if (age < 1) {
			return PUPPY;
		}
		if (age >= 9) {
			return SENIOR;
		}
		return ADULT;
	}
}
