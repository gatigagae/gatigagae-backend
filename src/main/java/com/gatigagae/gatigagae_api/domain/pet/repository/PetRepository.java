package com.gatigagae.gatigagae_api.domain.pet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gatigagae.gatigagae_api.domain.pet.entity.Pet;

public interface PetRepository extends JpaRepository<Pet, Long> {

	Optional<Pet> findByUserId(Long userId);
}
