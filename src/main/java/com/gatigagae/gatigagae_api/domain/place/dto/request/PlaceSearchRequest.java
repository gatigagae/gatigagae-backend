package com.gatigagae.gatigagae_api.domain.place.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "반려동물 동반 가능 장소 검색 요청")
public record PlaceSearchRequest(

	@Schema(description = "검색어", example = "근처에 콩이랑 갈 수 있는 카페 찾아줘")
	@NotBlank(message = "검색어는 필수입니다.")
	@Size(max = 200)
	String text,

	@Schema(description = "위도", example = "37.555198169366435")
	@NotNull(message = "위도는 필수입니다.")
	@DecimalMin(value = "-90", message = "위도는 -90에서 90 사이여야 합니다.")
	@DecimalMax(value = "90", message = "위도는 -90에서 90 사이여야 합니다.")
	Double latitude,

	@Schema(description = "경도", example = "126.93698075993808")
	@NotNull(message = "경도는 필수입니다.")
	@DecimalMin(value = "-180", message = "경도는 -180에서 180 사이여야 합니다.")
	@DecimalMax(value = "180", message = "경도는 -180에서 180 사이여야 합니다.")
	Double longitude,

	@Schema(description = "검색 반경(m). 생략하면 5000", example = "5000")
	@Min(value = 1, message = "반경은 1m 이상이어야 합니다.")
	@Max(value = 20_000, message = "반경은 20km 이하여야 합니다.")
	Integer radius,

	@Schema(description = "페이지 번호. 0부터 시작하고, 생략하면 0", example = "0")
	@Min(value = 0, message = "페이지는 0 이상이어야 합니다.")
	Integer page

) {
}
