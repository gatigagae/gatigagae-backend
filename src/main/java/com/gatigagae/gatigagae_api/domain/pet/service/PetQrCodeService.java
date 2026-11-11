package com.gatigagae.gatigagae_api.domain.pet.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gatigagae.gatigagae_api.domain.pet.dto.response.PetQrCodeResponse;
import com.gatigagae.gatigagae_api.domain.pet.dto.response.PetQrCodeStatusResponse;
import com.gatigagae.gatigagae_api.domain.pet.entity.PetQrCode;
import com.gatigagae.gatigagae_api.domain.pet.exception.PetErrorCode;
import com.gatigagae.gatigagae_api.domain.pet.repository.PetQrCodeRepository;
import com.gatigagae.gatigagae_api.domain.user.repository.UserRepository;
import com.gatigagae.gatigagae_api.global.apipayload.exception.GeneralException;
import com.gatigagae.gatigagae_api.global.util.HashUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PetQrCodeService {

	private static final Duration VALID_DURATION = Duration.ofMinutes(10);

	private final PetQrCodeRepository petQrCodeRepository;
	private final UserRepository userRepository;

	@Value("${app.profile-page-url}")
	private String profilePageUrl;

	@Transactional
	public PetQrCodeResponse issue(Long userId) {
		String code = UUID.randomUUID().toString();
		String codeHash = HashUtil.sha256(code);
		LocalDateTime expiresAt = LocalDateTime.now().plus(VALID_DURATION);

		petQrCodeRepository.findByUserId(userId).ifPresentOrElse(
			qrCode -> qrCode.reissue(codeHash, expiresAt),
			() -> petQrCodeRepository.save(
				PetQrCode.create(userRepository.getReferenceById(userId), codeHash, expiresAt)));

		return new PetQrCodeResponse(profilePageUrl + "?code=" + code, VALID_DURATION.toSeconds());
	}

	@Transactional(readOnly = true)
	public PetQrCodeStatusResponse getStatus(Long userId) {
		PetQrCode qrCode = petQrCodeRepository.findByUserId(userId)
			.orElseThrow(() -> new GeneralException(PetErrorCode.QR_CODE_NOT_FOUND));

		return new PetQrCodeStatusResponse(qrCode.status(LocalDateTime.now()));
	}

	public PetQrCode getUsableQrCode(String code) {
		return Optional.ofNullable(code)
			.map(HashUtil::sha256)
			.flatMap(petQrCodeRepository::findByCodeHash)
			.filter(qrCode -> qrCode.isUsable(LocalDateTime.now()))
			.orElseThrow(() -> new GeneralException(PetErrorCode.INVALID_QR_CODE));
	}
}