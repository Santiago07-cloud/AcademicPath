package com.academicpath.backend.service;

public interface PasswordResetService {
    /**
     * Solicita la recuperación de contraseña enviando un correo
     * @param correo Correo del usuario
     */
    void solicitarRecuperacion(String correo);

    /**
     * Valida si un token de recuperación es válido
     * @param token Token a validar
     */
    void validarToken(String token);

    /**
     * Restablece la contraseña de un usuario usando su token
     * @param token Token de recuperación
     * @param nuevaContrasena Nueva contraseña
     */
    void resetearContrasena(String token, String nuevaContrasena);
}

