package com.academicpath.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI/Swagger <br>
 * 
 * @author Academic Path
 */
@Configuration
public class OpenAPIConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("Academic Path - API Usuarios")
						.version("1.0.0")
						.description("API REST para gestionar usuarios en Academic Path")
						.contact(new Contact()
								.name("Academic Path")
								.url("http://www.academicpath.com")
								.email("info@academicpath.com"))
						.license(new License()
								.name("Apache 2.0")
								.url("http://springdoc.org")))
				.components(new Components()
						.addSecuritySchemes("bearerAuth",
								new SecurityScheme()
										.type(SecurityScheme.Type.HTTP)
										.scheme("bearer")
										.bearerFormat("JWT")
										.description("Ingresa el token JWT obtenido en /auth/login")))
				.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
	}
}

