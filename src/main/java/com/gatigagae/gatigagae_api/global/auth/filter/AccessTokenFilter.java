package com.gatigagae.gatigagae_api.global.auth.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.gatigagae.gatigagae_api.domain.user.service.UserService;
import com.gatigagae.gatigagae_api.global.apipayload.code.GeneralErrorCode;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AccessTokenFilter extends OncePerRequestFilter {

	public static final String AUTH_ERROR = AccessTokenFilter.class.getName() + ".AUTH_ERROR";
	private static final String BEARER_PREFIX = "Bearer ";

	private final UserService userService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (authorization != null && authorization.startsWith(BEARER_PREFIX)) {
			userService.findUserId(authorization.substring(BEARER_PREFIX.length()))
				.ifPresentOrElse(this::authenticate,
					() -> request.setAttribute(AUTH_ERROR, GeneralErrorCode.INVALID_TOKEN));
		}

		filterChain.doFilter(request, response);
	}

	private void authenticate(Long userId) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(UsernamePasswordAuthenticationToken.authenticated(userId, null, List.of()));
		SecurityContextHolder.setContext(context);
	}
}
