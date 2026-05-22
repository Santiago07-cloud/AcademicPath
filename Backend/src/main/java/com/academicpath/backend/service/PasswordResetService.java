package com.academicpath.backend.service;

public interface PasswordResetService {
    void solicitarRecuperacion(String correo);
    void validarToken(String token);
    void resetearContrasena(String token, String nuevaContrasena);
}
