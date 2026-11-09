package com.gatigagae.gatigagae_api.domain.place.dto.response;

import java.util.List;

import com.gatigagae.gatigagae_api.domain.place.entity.enums.PlaceCategory;
import com.gatigagae.gatigagae_api.global.client.tour.dto.TourPlaceResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "반려동물 동반 가능 장소 검색 목록 응답")
public record PlaceSearchListResponse(

	@Schema(description = "알아들은 장소 종류. 종류 없이 장소를 찾으면 `ALL`, 해석하지 못하면 `null`", example = "CAFE")
	PlaceCategory category,

	@Schema(description = "가까운 순으로 최대 3곳")
	List<PlaceSearchResponse> places,

	@Schema(description = "다음 페이지가 있는지 여부", example = "true")
	boolean hasNext

) {
	public static PlaceSearchListResponse notUnderstood() {
		return new PlaceSearchListResponse(null, List.of(), false);
	}

	@Schema(description = "반려동물 동반 가능 장소 검색 응답")
	public record PlaceSearchResponse(

		@Schema(description = "관광공사 콘텐츠 ID", example = "264353")
		String contentId,

		@Schema(description = "장소 이름", example = "카페 A")
		String name,

		@Schema(description = "주소", example = "서울특별시 종로구 효자로7길 7-5 (통의동)")
		String address,

		@Schema(description = "위도", example = "37.5787999537")
		double latitude,

		@Schema(description = "경도", example = "126.9734877974")
		double longitude,

		@Schema(description = "검색 위치에서의 거리(m)", example = "794")
		int distance,

		@Schema(description = "전화번호", example = "010-1111-1111")
		String tel,

		@Schema(description = "반려동물 동반 조건. 정보가 없으면 빈 배열", example = "[\"전 견종 동반 가능\", \"전구역 동반가능\", \"목줄 착용\"]")
		List<String> conditions
	) {

		public static PlaceSearchResponse of(TourPlaceResponse place, List<String> conditions) {
			return new PlaceSearchResponse(
				place.contentId(),
				place.name(),
				place.address(),
				place.latitude(),
				place.longitude(),
				place.distance(),
				place.tel(),
				conditions);
		}

	}

}
