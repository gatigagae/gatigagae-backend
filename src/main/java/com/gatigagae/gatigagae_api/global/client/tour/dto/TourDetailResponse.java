package com.gatigagae.gatigagae_api.global.client.tour.dto;

import java.util.List;
import java.util.stream.Stream;

public record TourDetailResponse(

	String allowedAnimals,
	String accompanyType,
	String requirements

) {

	public List<String> conditions() {
		return Stream.of(allowedAnimals, accompanyType, requirements)
			.filter(value -> value != null && !value.isBlank())
			.map(String::strip)
			.toList();
	}
}