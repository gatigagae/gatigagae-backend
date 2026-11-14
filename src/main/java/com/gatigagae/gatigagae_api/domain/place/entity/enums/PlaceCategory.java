package com.gatigagae.gatigagae_api.domain.place.entity.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PlaceCategory {

	CAFE(List.of("카페", "까페", "커피")),
	RESTAURANT(List.of("식당", "음식점", "밥집", "맛집", "밥먹", "레스토랑", "배고")),
	PARK(List.of("공원", "산책")),
	ATTRACTION(List.of("관광", "여행지", "명소", "박물관", "미술관", "구경")),
	HOSPITAL(List.of("병원", "진료", "응급")),
	PHARMACY(List.of("약국")),
	GROOMING(List.of("미용", "목욕", "그루밍")),
	PET_HOTEL(List.of("맡길", "맡기", "맡겨", "위탁", "유치원", "호텔링", "강아지호텔", "애견호텔", "펫호텔")),
	ACCOMMODATION(List.of("숙소", "숙박", "펜션", "호텔", "묵을")),
	PET_SHOP(List.of("용품", "사료", "간식", "펫샵", "애견샵")),

	// 종류를 말하지 않은 장소 요청
	ALL(List.of("갈만한", "갈수있는", "갈데", "갈곳", "어디갈"));

	private final List<String> keywords;

	// 공백과 문장부호를 지운 문장을 받는다. 여러 종류가 섞이면 먼저 선언된 종류를 고른다.
	public static Optional<PlaceCategory> from(String text) {
		return Arrays.stream(values())
			.filter(category -> category.keywords.stream().anyMatch(text::contains))
			.findFirst();
	}

	// 이 종류로 찾을 때 검색할 장소 종류
	public List<PlaceCategory> targets() {
		return this == ALL ? List.of(CAFE, RESTAURANT, PARK, ATTRACTION) : List.of(this);
	}
}
