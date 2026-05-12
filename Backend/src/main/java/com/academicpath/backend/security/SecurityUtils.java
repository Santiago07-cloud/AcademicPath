package com.academicpath.backend.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public Long getCurrentUserId() {
        UsuarioUserDetails userDetails = getCurrentUserDetails();
        return userDetails.getId();
    }

    public boolean isAdmin() {
        return getCurrentUserDetails()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    /**
     * Lanza AccessDeniedException si el usuario actual no es dueño del recurso ni Admin.
     */
    public void validarPropietario(Long propietarioId) {
        if (!isAdmin() && !getCurrentUserId().equals(propietarioId)) {
            throw new AccessDeniedException("No tienes permisos para acceder a este recurso");
        }
    }

    private UsuarioUserDetails getCurrentUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (UsuarioUserDetails) auth.getPrincipal();
    }
}
