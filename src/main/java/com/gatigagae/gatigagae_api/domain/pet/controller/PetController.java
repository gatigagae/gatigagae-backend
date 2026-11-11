package com.gatigagae.gatigagae_api.domain.pet.controller;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gatigagae.gatigagae_api.domain.pet.dto.request.PetSaveRequest;
import com.gatigagae.gatigagae_api.domain.pet.dto.request.TemperatureRequest;
import com.gatigagae.gatigagae_api.domain.pet.dto.response.BreedResponse;
import com.gatigagae.gatigagae_api.domain.pet.dto.response.PetQrCodeResponse;
import com.gatigagae.gatigagae_api.domain.pet.dto.response.PetQrCodeStatusResponse;
import com.gatigagae.gatigagae_api.domain.pet.dto.response.PetResponse;
import com.gatigagae.gatigagae_api.domain.pet.dto.response.TemperatureResponse;
import com.gatigagae.gatigagae_api.domain.pet.service.PetQrCodeService;
import com.gatigagae.gatigagae_api.domain.pet.service.PetService;
import com.gatigagae.gatigagae_api.global.apipayload.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/pets")
@RequiredArgsConstructor
@Tag(name = "Pet", description = "반려동물 API")
public class PetController {

	private final PetService petService;
	private final PetQrCodeService petQrCodeService;

	@Operation(summary = "견종 목록", description = "프로필 입력 화면의 견종 목록입니다.")
	@GetMapping("/breeds")
	public ApiResponse<List<BreedResponse>> getBreeds() {

		List<BreedResponse> response = petService.getBreeds();

		return ApiResponse.onSuccess("견종 목록 조회에 성공했습니다.", response);
	}

	@Operation(summary = "맞춤 실내 온도 미리보기", description = "프로필 입력 화면에서 값을 바꿀 때마다 불러, 저장하기 전에 맞춤 실내 온도를 보여줍니다.")
	@GetMapping("/temperature")
	public ApiResponse<TemperatureResponse> previewTemperature(
		@Valid @ParameterObject @ModelAttribute TemperatureRequest request
	) {
		TemperatureResponse response = petService.previewTemperature(request);

		return ApiResponse.onSuccess("맞춤 실내 온도 조회에 성공했습니다.", response);
	}

	@Operation(summary = "내 반려견 조회", description = "사용자가 등록한 반려견을 조회합니다.")
	@GetMapping("/me")
	public ApiResponse<PetResponse> getMyPet(
		@AuthenticationPrincipal Long userId
	) {
		PetResponse response = petService.getMyPet(userId);

		return ApiResponse.onSuccess("반려견 조회에 성공했습니다.", response);
	}

	@Operation(summary = "프로필 작성 QR 코드 발급", description = "프로필 작성을 위한 QR 코드를 발급합니다.")
	@PostMapping("/me/qr-code")
	public ApiResponse<PetQrCodeResponse> issueQrCode(
		@AuthenticationPrincipal Long userId
	) {
		PetQrCodeResponse response = petQrCodeService.issue(userId);

		return ApiResponse.onSuccess("QR 코드 발급에 성공했습니다.", response);
	}

	@Operation(summary = "QR 코드 입력 상태",
		description = "차량에서 QR 코드를 띄운 동안 지속적으로 확인합니다. `COMPLETED`가 되면 내 반려견을 다시 조회합니다.")
	@GetMapping("/me/qr-code")
	public ApiResponse<PetQrCodeStatusResponse> getQrCodeStatus(
		@AuthenticationPrincipal Long userId
	) {
		PetQrCodeStatusResponse response = petQrCodeService.getStatus(userId);

		return ApiResponse.onSuccess("QR 코드 상태 조회에 성공했습니다.", response);
	}

	@Operation(summary = "QR 코드로 반려견 정보 조회", description = "웹에서 수정 화면을 채울 때 호출합니다.")
	@GetMapping("/profile")
	public ApiResponse<PetResponse> getPetByQrCode(
		@Parameter(description = "QR 주소의 code 값") @RequestParam(required = false) String code
	) {
		PetResponse response = petService.getPetByQrCode(code);

		return ApiResponse.onSuccess("반려견 조회에 성공했습니다.", response);
	}

	@Operation(summary = "QR 코드로 반려견 정보 저장", description = "웹에서 저장할 때 호출합니다.")
	@PostMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ApiResponse<PetResponse> savePetByQrCode(
		@Parameter(description = "QR 주소의 code 값") @RequestParam(required = false) String code,
		@Valid @ModelAttribute PetSaveRequest request
	) {
		PetResponse response = petService.savePetByQrCode(code, request);

		return ApiResponse.onSuccess("반려견 저장에 성공했습니다.", response);
	}
}
