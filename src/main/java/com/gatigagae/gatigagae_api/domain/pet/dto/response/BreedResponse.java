package com.gatigagae.gatigagae_api.domain.pet.dto.response;

import com.gatigagae.gatigagae_api.domain.pet.entity.enums.Breed;
import com.gatigagae.gatigagae_api.domain.pet.entity.enums.CoatType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "견종")
public record BreedResponse(

	@Schema(description = "견종 코드", example = "MALTESE")
	Breed breed,

	@Schema(description = "목록에 보여줄 이름", example = "말티즈")
	String name,

	@Schema(description = "털 유형 기본값. 견종만으로 정할 수 없으면 null", example = "SINGLE")
	CoatType coatType,

	@Schema(description = "단두종 기본값. 견종만으로 정할 수 없으면 null", example = "false")
	Boolean brachycephalic,

	@Schema(description = "견종 기본 사진 주소. 프로필 사진을 올리지 않았으면 이 사진을 보여준다",
		example = "https://gatigagae.s3.ap-northeast-2.amazonaws.com/breeds/maltese.png")
	String defaultImageUrl

) {
	public static BreedResponse from(Breed breed, String defaultImageUrl) {
		String name = breed.getDisplayName();
		return new BreedResponse(breed, name, breed.getCoatType(), breed.getBrachycephalic(), defaultImageUrl);
	}
}