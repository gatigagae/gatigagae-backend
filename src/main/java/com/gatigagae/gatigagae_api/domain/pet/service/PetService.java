package com.gatigagae.gatigagae_api.domain.pet.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.gatigagae.gatigagae_api.domain.pet.dto.request.PetSaveRequest;
import com.gatigagae.gatigagae_api.domain.pet.dto.request.TemperatureRequest;
import com.gatigagae.gatigagae_api.domain.pet.dto.response.BreedResponse;
import com.gatigagae.gatigagae_api.domain.pet.dto.response.PetResponse;
import com.gatigagae.gatigagae_api.domain.pet.dto.response.TemperatureResponse;
import com.gatigagae.gatigagae_api.domain.pet.entity.Pet;
import com.gatigagae.gatigagae_api.domain.pet.entity.PetQrCode;
import com.gatigagae.gatigagae_api.domain.pet.entity.enums.Breed;
import com.gatigagae.gatigagae_api.domain.pet.exception.PetErrorCode;
import com.gatigagae.gatigagae_api.domain.pet.repository.PetRepository;
import com.gatigagae.gatigagae_api.global.apipayload.exception.GeneralException;
import com.gatigagae.gatigagae_api.global.s3.S3Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PetService {

	private static final String PROFILE_IMAGE_DIR = "pets";
	private static final Set<String> IMAGE_TYPES = Set.of(
		"image/jpeg",
		"image/png",
		"image/webp"
	);

	private final PetRepository petRepository;
	private final PetQrCodeService petQrCodeService;
	private final S3Service s3Service;

	public List<BreedResponse> getBreeds() {
		return Arrays.stream(Breed.values())
			.map(BreedResponse::from)
			.toList();
	}

	public TemperatureResponse previewTemperature(TemperatureRequest request) {
		return TemperatureResponse.of(
			request.breed(),
			request.coatType(),
			request.brachycephalic(),
			request.age(),
			request.weight()
		);
	}

	@Transactional(readOnly = true)
	public PetResponse getMyPet(Long userId) {
		return findPet(userId);
	}

	@Transactional(readOnly = true)
	public PetResponse getPetByQrCode(String code) {
		PetQrCode qrCode = petQrCodeService.getUsableQrCode(code);

		return findPet(qrCode.getUser().getId());
	}

	@Transactional
	public PetResponse savePetByQrCode(String code, PetSaveRequest request) {
		PetQrCode qrCode = petQrCodeService.getUsableQrCode(code);
		Pet pet = petRepository.findByUserId(qrCode.getUser().getId())
			.orElseGet(() -> Pet.builder().user(qrCode.getUser()).build());

		pet.update(
			request.name(),
			request.breed(),
			request.customBreedName(),
			request.coatType(),
			request.brachycephalic(),
			request.age(),
			request.weight(),
			request.note()
		);
		if (request.profileImage() != null && !request.profileImage().isEmpty()) {
			uploadProfileImage(pet, request.profileImage());
		}

		petRepository.save(pet);
		qrCode.use(LocalDateTime.now());

		return toResponse(pet);
	}

	private PetResponse findPet(Long userId) {
		return petRepository.findByUserId(userId)
			.map(this::toResponse)
			.orElseThrow(() -> new GeneralException(PetErrorCode.PET_NOT_FOUND));
	}

	private void uploadProfileImage(Pet pet, MultipartFile image) {
		String contentType = image.getContentType();
		if (contentType == null || !IMAGE_TYPES.contains(contentType)) {
			throw new GeneralException(PetErrorCode.INVALID_IMAGE_TYPE);
		}

		String key = s3Service.replace(PROFILE_IMAGE_DIR, image, pet.getProfileImageKey());
		pet.changeProfileImage(key);
	}

	private PetResponse toResponse(Pet pet) {
		return PetResponse.from(pet, s3Service.getUrl(pet.getProfileImageKey()));
	}
}
