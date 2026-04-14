package com.academicpath.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de Spring Security <br>
 * 
 * @author Academic Path
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests(authz -> authz
				// Permitir acceso público a endpoints de usuarios
				.requestMatchers("/usuarios/**").permitAll()
				// Permitir acceso a Swagger/OpenAPI
				.requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
				// El resto requiere autenticación
				.anyRequest().authenticated()
			)
			.csrf(csrf -> csrf.disable())
			.httpBasic(basic -> {});
		
		return http.build();
	}
}

