package com.gatigagae.gatigagae_api.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 등록 응답")
public record UserCreateResponse(

	@Schema(description = "로그인이 필요한 API의 Authorization 헤더에 `Bearer {accessToken}`으로 보내는 토큰",
		example = "3f2b8c1e-7d4a-4b9e-9c61-2a5f0e8d7b34")
	String accessToken

) {
}
