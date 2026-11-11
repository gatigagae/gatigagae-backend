package com.gatigagae.gatigagae_api.domain.pet.dto.request;

import java.math.BigDecimal;

import org.springframework.web.multipart.MultipartFile;

import com.gatigagae.gatigagae_api.domain.pet.entity.enums.Breed;
import com.gatigagae.gatigagae_api.domain.pet.entity.enums.CoatType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "반려견 프로필 저장 요청")
public record PetSaveRequest(

	@Schema(description = "이름", example = "콩이")
	@NotBlank(message = "이름은 필수입니다.")
	@Size(max = 50, message = "이름은 50자 이하여야 합니다.")
	String name,

	@Schema(description = "견종. 목록에 없으면 OTHER", example = "OTHER")
	@NotNull(message = "견종은 필수입니다.")
	Breed breed,

	@Schema(description = "직접 적은 견종 이름. 견종이 OTHER일 때만 저장합니다.", example = "믹스견")
	@Size(max = 30, message = "견종 이름은 30자 이하여야 합니다.")
	String customBreedName,

	@Schema(description = "털 유형. `DOUBLE`: 이중모, `SINGLE`: 단일모", example = "SINGLE")
	@NotNull(message = "털 유형은 필수입니다.")
	CoatType coatType,

	@Schema(description = "단두종 여부", example = "false")
	@NotNull(message = "단두종 여부는 필수입니다.")
	Boolean brachycephalic,

	@Schema(description = "나이", example = "4")
	@NotNull(message = "나이는 필수입니다.")
	@Min(value = 0, message = "나이는 0살 이상이어야 합니다.")
	@Max(value = 30, message = "나이는 30살 이하여야 합니다.")
	Integer age,

	@Schema(description = "몸무게", example = "4.2")
	@DecimalMin(value = "0", inclusive = false, message = "몸무게는 0보다 커야 합니다.")
	@Digits(integer = 3, fraction = 2, message = "몸무게는 999.99kg 이하, 소수점 둘째 자리까지 입력할 수 있습니다.")
	BigDecimal weight,

	@Schema(description = "특이사항", example = "차멀미가 있어요")
	@Size(max = 200, message = "특이사항은 200자 이하여야 합니다.")
	String note,

	@Schema(description = "프로필 사진. jpg, png, webp, 10MB 이하.")
	MultipartFile profileImage

) {
}