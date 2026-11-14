package com.gatigagae.gatigagae_api.domain.place.dto.response;

import java.util.List;

import com.gatigagae.gatigagae_api.domain.place.entity.Place;
import com.gatigagae.gatigagae_api.domain.place.entity.enums.PlaceCategory;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "반려동물 동반 가능 장소 검색 목록 응답")
public record PlaceSearchListResponse(

	@Schema(description = "알아들은 장소 종류. 종류 없이 장소를 찾으면 `ALL`, 해석하지 못하면 `null`", example = "CAFE")
	PlaceCategory category,

	@Schema(description = "가까운 순으로 최대 3곳")
	List<PlaceSearchResponse> places,

	@Schema(description = "다음 페이지가 있는지 여부", example = "true")
	boolean hasNext,

	@Schema(description = "찾은 전체 장소 수", example = "20")
	int totalCount

) {
	public static PlaceSearchListResponse notUnderstood() {
		return new PlaceSearchListResponse(null, List.of(), false, 0);
	}

	@Schema(description = "반려동물 동반 가능 장소 검색 응답")
	public record PlaceSearchResponse(

		@Schema(description = "장소 ID", example = "1")
		Long placeId,

		@Schema(description = "장소 이름", example = "카페 A")
		String name,

		@Schema(description = "위도", example = "37.5787999537")
		double latitude,

		@Schema(description = "경도", example = "126.9734877974")
		double longitude,

		@Schema(description = "요청한 위치에서의 거리(m)", example = "794")
		int distance

	) {

		public static PlaceSearchResponse of(Place place, int distance) {
			return new PlaceSearchResponse(
				place.getId(),
				place.getName(),
				place.getLatitude(),
				place.getLongitude(),
				distance
			);
		}

	}

}
