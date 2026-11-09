package com.gatigagae.gatigagae_api.global.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

	@Value("${swagger.server-url}")
	private String serverUrl;

	@Value("${swagger.server-description}")
	private String serverDescription;

	@Bean
	public OpenAPI openApi() {
		Server server = new Server()
			.url(serverUrl)
			.description(serverDescription);

		return new OpenAPI()
			.info(apiInfo())
			.servers(List.of(server));
	}

	private Info apiInfo() {
		return new Info()
			.title("같이가개 API Documentation")
			.description("같이가개 Swagger 문서입니다.")
			.version("1.0.0");
	}

}