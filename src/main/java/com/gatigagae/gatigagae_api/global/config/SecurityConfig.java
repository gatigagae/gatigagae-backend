package com.gatigagae.gatigagae_api.global.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.gatigagae.gatigagae_api.global.auth.filter.AccessTokenFilter;
import com.gatigagae.gatigagae_api.global.auth.handler.ApiAuthenticationEntryPoint;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final AccessTokenFilter accessTokenFilter;
	private final ApiAuthenticationEntryPoint authenticationEntryPoint;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http
			// csrf 비활성화
			.csrf(AbstractHttpConfigurer::disable)
			// form login 비활성화
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			// 기본 /logout 비활성화
			.logout(AbstractHttpConfigurer::disable)
			// 세션 stateless
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			// 인증/인가 예외 처리
			.exceptionHandling(exception -> exception
				.authenticationEntryPoint(authenticationEntryPoint)
			)
			// api 접근 권한 설정
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
				.requestMatchers("/api/v1/places/**").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/v1/pets/breeds", "/api/v1/pets/temperature").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/v1/pets/profile").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/v1/pets/profile").permitAll()
				.requestMatchers(HttpMethod.GET, "/profile", "/profile/**").permitAll()
				.requestMatchers("/actuator/health").permitAll()
				.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
				.requestMatchers("/error").permitAll()
				.anyRequest().authenticated()
			)
			.addFilterBefore(accessTokenFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public FilterRegistrationBean<AccessTokenFilter> accessTokenFilterRegistration(AccessTokenFilter filter) {

		FilterRegistrationBean<AccessTokenFilter> registration = new FilterRegistrationBean<>(filter);
		registration.setEnabled(false);

		return registration;
	}
}
