package com.gatigagae.gatigagae_api.domain.place.dto.response;

import com.gatigagae.gatigagae_api.domain.place.entity.Place;
import com.gatigagae.gatigagae_api.domain.place.entity.enums.PlaceCategory;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "반려동물 동반 가능 장소 상세 응답")
public record PlaceDetailResponse(

	@Schema(description = "장소 ID", example = "1")
	Long placeId,

	@Schema(description = "장소 이름", example = "카페 A")
	String name,

	@Schema(description = "장소 종류", example = "CAFE")
	PlaceCategory category,

	@Schema(description = "주소", example = "서울특별시 종로구 효자로7길 7-5")
	String address,

	@Schema(description = "위도", example = "37.5787999537")
	double latitude,

	@Schema(description = "경도", example = "126.9734877974")
	double longitude,

	@Schema(description = "요청한 위치에서의 거리(m)", example = "794")
	int distance,

	@Schema(description = "전화번호. 정보가 없으면 null", example = "010-1111-1111")
	String tel,

	@Schema(description = "홈페이지. 정보가 없으면 null", example = "https://example.com")
	String homepage,

	@Schema(description = "운영시간. 정보가 없으면 null", example = "매일 10:00~22:00")
	String businessHours,

	@Schema(description = "휴무일. 정보가 없으면 null", example = "매주 월요일")
	String closedDays,

	@Schema(description = "주차 가능 여부. 정보가 없으면 null", example = "true")
	Boolean parkingAvailable,

	@Schema(description = "함께 들어갈 수 있는 반려동물 크기. 정보가 없으면 null", example = "모두 가능")
	String allowedSize,

	@Schema(description = "반려동물 동반 제한 사항. 제한 사항이 없으면 null", example = "목줄, 배변봉투")
	String restriction
) {

	public static PlaceDetailResponse from(Place place, int distance) {
		return new PlaceDetailResponse(
			place.getId(),
			place.getName(),
			place.getCategory(),
			place.getAddress(),
			place.getLatitude(),
			place.getLongitude(),
			distance,
			place.getTel(),
			place.getHomepage(),
			place.getBusinessHours(),
			place.getClosedDays(),
			place.getParkingAvailable(),
			place.getAllowedSize(),
			place.getRestriction()
		);
	}
}
