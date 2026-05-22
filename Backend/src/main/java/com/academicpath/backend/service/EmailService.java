package com.academicpath.backend.service;

public interface EmailService {
    /**
     * Envía un correo de recuperación de contraseña
     * @param destinatario Correo del usuario
     * @param nombres Nombres del usuario
     * @param resetLink Link para restaurar la contraseña
     */
    void enviarCorreoRecuperacion(String destinatario, String nombres, String resetLink);
}

