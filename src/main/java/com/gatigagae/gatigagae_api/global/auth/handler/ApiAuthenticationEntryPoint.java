package com.gatigagae.gatigagae_api.global.auth.handler;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.gatigagae.gatigagae_api.global.apipayload.code.BaseErrorCode;
import com.gatigagae.gatigagae_api.global.apipayload.code.GeneralErrorCode;
import com.gatigagae.gatigagae_api.global.apipayload.exception.GeneralException;
import com.gatigagae.gatigagae_api.global.auth.filter.AccessTokenFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final HandlerExceptionResolver handlerExceptionResolver;

	public ApiAuthenticationEntryPoint(
		@Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver) {
		this.handlerExceptionResolver = handlerExceptionResolver;
	}

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) {
		BaseErrorCode errorCode = request.getAttribute(AccessTokenFilter.AUTH_ERROR) instanceof BaseErrorCode code
			? code
			: GeneralErrorCode.MISSING_AUTH_INFO;

		handlerExceptionResolver.resolveException(request, response, null, new GeneralException(errorCode));
	}
}
