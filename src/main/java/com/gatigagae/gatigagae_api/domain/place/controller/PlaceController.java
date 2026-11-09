package com.gatigagae.gatigagae_api.domain.place.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gatigagae.gatigagae_api.domain.place.dto.request.PlaceSearchRequest;
import com.gatigagae.gatigagae_api.domain.place.dto.response.PlaceSearchListResponse;
import com.gatigagae.gatigagae_api.domain.place.service.PlaceService;
import com.gatigagae.gatigagae_api.global.apipayload.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/places")
@RequiredArgsConstructor
@Tag(name = "Place", description = "반려동물 동반 가능 장소 검색 API")
public class PlaceController {

	private final PlaceService placeService;

	@Operation(summary = "장소 검색",
		description = "검색어의 장소 종류를 찾아 가까운 3곳을 돌려준다. 종류를 찾지 못하면 category가 null이다. 재탐색은 같은 문장과 좌표에 page를 올려서 검색한다.")
	@GetMapping("/search")
	public ApiResponse<PlaceSearchListResponse> searchPlaces(
		@Valid @ParameterObject @ModelAttribute PlaceSearchRequest request
	) {
		PlaceSearchListResponse response = placeService.searchPlaces(request);

		return ApiResponse.onSuccess("장소 검색에 성공했습니다.", response);
	}
}
