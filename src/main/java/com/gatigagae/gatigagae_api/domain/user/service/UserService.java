package com.gatigagae.gatigagae_api.domain.user.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gatigagae.gatigagae_api.domain.user.dto.response.UserCreateResponse;
import com.gatigagae.gatigagae_api.domain.user.entity.User;
import com.gatigagae.gatigagae_api.domain.user.repository.UserRepository;
import com.gatigagae.gatigagae_api.global.util.HashUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	@Transactional
	public UserCreateResponse createUser() {
		String accessToken = UUID.randomUUID().toString();

		userRepository.save(User.builder()
			.tokenHash(HashUtil.sha256(accessToken))
			.build());
		return new UserCreateResponse(accessToken);
	}

	@Transactional(readOnly = true)
	public Optional<Long> findUserId(String accessToken) {
		return userRepository.findByTokenHash(HashUtil.sha256(accessToken))
			.map(User::getId);
	}
}
