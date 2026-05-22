package com.academicpath.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    /** Rutas que NUNCA necesitan JWT — el filtro las salta completamente */
    private static final List<String> PUBLIC_PATHS = List.of(
            "/auth/login",
            "/auth/register",
            "/auth/forgot-password",
            "/auth/reset-password",
            "/auth/reset-password/validate"
    );

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioUserDetailsService usuarioUserDetailsService;

    /**
     * Indica a Spring que este filtro NO debe ejecutarse para rutas públicas.
     * Esto evita cualquier interferencia incluso si llega un token inválido.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        boolean isPublic = PUBLIC_PATHS.stream()
                .anyMatch(pub -> PATH_MATCHER.match(pub, path));
        if (isPublic) {
            log.debug("JwtFilter SALTADO para ruta pública: {} {}", request.getMethod(), path);
        }
        return isPublic;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String path = request.getServletPath();
        log.debug("JwtFilter procesando: {} {}", request.getMethod(), path);

        try {
            String jwt = extractJwt(request);

            if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                String correo = jwtUtil.extractUsername(jwt);

                if (correo != null) {
                    UserDetails userDetails = usuarioUserDetailsService.loadUserByUsername(correo);

                    if (jwtUtil.isTokenValid(jwt, userDetails)) {
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails, null, userDetails.getAuthorities());
                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        log.debug("JWT válido para usuario: {}", correo);
                    }
                }
            }
        } catch (Exception ex) {
            // No propagar la excepción — dejar que Spring Security decida qué hacer
            log.warn("JWT inválido en {}: {}", path, ex.getMessage());
            SecurityContextHolder.clearContext();
        }

        chain.doFilter(request, response);
    }

    private String extractJwt(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7).trim();
            return token.isEmpty() ? null : token;
        }
        return null;
    }
}
