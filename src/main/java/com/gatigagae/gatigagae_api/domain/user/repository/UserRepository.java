package com.gatigagae.gatigagae_api.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gatigagae.gatigagae_api.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByTokenHash(String tokenHash);
}
