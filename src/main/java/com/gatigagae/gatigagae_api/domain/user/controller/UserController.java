package com.gatigagae.gatigagae_api.domain.user.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gatigagae.gatigagae_api.domain.user.dto.response.UserCreateResponse;
import com.gatigagae.gatigagae_api.domain.user.service.UserService;
import com.gatigagae.gatigagae_api.global.apipayload.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자 API")
public class UserController {

	private final UserService userService;

	@Operation(summary = "사용자 등록",
		description = "앱을 처음 실행할 때 한 번 호출합니다. 받은 accessToken을 기기에 저장해 두고, 로그인이 필요한 API에 `Authorization: Bearer {accessToken}` 헤더로 보냅니다.")
	@PostMapping
	public ApiResponse<UserCreateResponse> createUser() {

		UserCreateResponse response = userService.createUser();

		return ApiResponse.onSuccess("사용자 등록에 성공했습니다.", response);
	}
}