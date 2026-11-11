package com.gatigagae.gatigagae_api.domain.pet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gatigagae.gatigagae_api.domain.pet.entity.PetQrCode;

public interface PetQrCodeRepository extends JpaRepository<PetQrCode, Long> {

	Optional<PetQrCode> findByUserId(Long userId);

	Optional<PetQrCode> findByCodeHash(String codeHash);

}
