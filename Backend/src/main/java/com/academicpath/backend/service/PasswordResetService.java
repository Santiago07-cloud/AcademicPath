package com.academicpath.backend.service;

public interface PasswordResetService {
<<<<<<< HEAD
    void solicitarRecuperacion(String correo);
    void validarToken(String token);
    void resetearContrasena(String token, String nuevaContrasena);
}
=======
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

>>>>>>> 9754ef26dd20d0a8b3bc72447006849f61443a43
