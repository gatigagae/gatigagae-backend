package com.gatigagae.gatigagae_api.domain.user.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gatigagae.gatigagae_api.domain.user.dto.response.UserCreateResponse;
import com.gatigagae.gatigagae_api.domain.user.entity.User;
import com.gatigagae.gatigagae_api.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	private static String hash(String accessToken) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			return HexFormat.of().formatHex(digest.digest(accessToken.getBytes(StandardCharsets.UTF_8)));
		} catch (NoSuchAlgorithmException exception) {
			throw new IllegalStateException(exception);
		}
	}

	@Transactional
	public UserCreateResponse createUser() {
		String accessToken = UUID.randomUUID().toString();

		userRepository.save(User.builder()
			.tokenHash(hash(accessToken))
			.build());
		return new UserCreateResponse(accessToken);
	}

	@Transactional(readOnly = true)
	public Optional<Long> findUserId(String accessToken) {
		return userRepository.findByTokenHash(hash(accessToken))
			.map(User::getId);
	}
}
