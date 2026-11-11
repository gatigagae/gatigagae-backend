package com.gatigagae.gatigagae_api.domain.place.entity.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PlaceCategory {

	CAFE(List.of("카페", "까페", "커피"), "FD", "FD05", Set.of()),
	RESTAURANT(List.of("식당", "음식점", "밥집", "맛집", "밥먹", "레스토랑", "배고"), "FD", null, Set.of("FD04", "FD05")),
	PARK(List.of("공원", "산책"), "VE", "VE03", Set.of()),

	// 종류를 말하지 않은 장소 요청
	ALL(List.of("갈만한", "갈수있는", "갈데", "갈곳", "어디갈"), null, null, Set.of("FD04"));

	private final List<String> keywords;
	private final String lclsSystm1;
	private final String lclsSystm2;
	private final Set<String> excludedLclsSystm2;

	// 공백과 문장부호를 지운 문장을 받는다. 여러 종류가 섞이면 먼저 선언된 종류를 고른다
	public static Optional<PlaceCategory> from(String text) {
		return Arrays.stream(values())
			.filter(category -> category.keywords.stream().anyMatch(text::contains))
			.findFirst();
	}

	public boolean includes(String lclsSystm2) {
		return !excludedLclsSystm2.contains(lclsSystm2);
	}
}
