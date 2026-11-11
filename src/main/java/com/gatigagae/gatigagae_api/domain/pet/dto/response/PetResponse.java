package com.gatigagae.gatigagae_api.domain.pet.dto.response;

import java.math.BigDecimal;

import com.gatigagae.gatigagae_api.domain.pet.entity.Pet;
import com.gatigagae.gatigagae_api.domain.pet.entity.enums.Breed;
import com.gatigagae.gatigagae_api.domain.pet.entity.enums.CoatType;
import com.gatigagae.gatigagae_api.domain.pet.entity.enums.TemperatureRule;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "반려견 프로필 응답")
public record PetResponse(

	@Schema(description = "이름", example = "보리")
	String name,

	@Schema(description = "견종 코드", example = "OTHER")
	Breed breed,

	@Schema(description = "화면에 보여줄 견종 이름. 직접 적은 이름이 있으면 그 이름", example = "믹스견")
	String breedName,

	@Schema(description = "직접 적은 견종 이름. 수정 화면을 채울 때 쓴다", example = "믹스견")
	String customBreedName,

	@Schema(description = "털 유형. `DOUBLE`: 이중모, `SINGLE`: 단일모", example = "SINGLE")
	CoatType coatType,

	@Schema(description = "단두종 여부", example = "false")
	boolean brachycephalic,

	@Schema(description = "나이", example = "4")
	int age,

	@Schema(description = "몸무게. 입력하지 않았으면 null", example = "4.2")
	BigDecimal weight,

	@Schema(description = "특이사항. 입력하지 않았으면 null", example = "차멀미가 있어요")
	String note,

	@Schema(description = "맞춤 실내 온도", example = "23")
	int temperature,

	@Schema(description = "프로필 사진 주소",
		example = "https://gatigagae.s3.ap-northeast-2.amazonaws.com/pets/3f2b8c1e-7d4a-4b9e-9c61-2a5f0e8d7b34")
	String profileImageUrl

) {

	public static PetResponse from(Pet pet, String profileImageUrl) {
		String breedName =
			pet.getCustomBreedName() != null ? pet.getCustomBreedName() : pet.getBreed().getDisplayName();

		TemperatureRule temperatureRule = TemperatureRule.of(pet.getBreed(), pet.getCoatType(),
			pet.getBrachycephalic(), pet.getAge(), pet.getWeight());

		return new PetResponse(
			pet.getName(),
			pet.getBreed(),
			breedName,
			pet.getCustomBreedName(),
			pet.getCoatType(),
			pet.getBrachycephalic(),
			pet.getAge(),
			pet.getWeight(),
			pet.getNote(),
			temperatureRule.getTemperature(),
			profileImageUrl
		);
	}
}
