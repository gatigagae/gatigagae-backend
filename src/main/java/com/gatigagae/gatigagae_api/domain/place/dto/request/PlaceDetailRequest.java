package com.gatigagae.gatigagae_api.domain.place.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@Schema(description = "반려동물 동반 가능 장소 상세 조회 요청")
public record PlaceDetailRequest(

	@Schema(description = "현재 위치의 위도", example = "37.555198169366435")
	@NotNull(message = "위도는 필수입니다.")
	@DecimalMin(value = "-90", message = "위도는 -90에서 90 사이여야 합니다.")
	@DecimalMax(value = "90", message = "위도는 -90에서 90 사이여야 합니다.")
	Double latitude,

	@Schema(description = "현재 위치의 경도", example = "126.93698075993808")
	@NotNull(message = "경도는 필수입니다.")
	@DecimalMin(value = "-180", message = "경도는 -180에서 180 사이여야 합니다.")
	@DecimalMax(value = "180", message = "경도는 -180에서 180 사이여야 합니다.")
	Double longitude

) {
}
