package com.academicpath.backend.service;

/**
 * Servicio para envío de correos electrónicos.
 */
public interface EmailService {

    /**
     * Envía el correo de recuperación de contraseña.
     *
     * @param destinatario correo del usuario
     * @param nombres      nombre del usuario para personalizar el mensaje
     * @param resetLink    URL completa con el token de recuperación
     */
    void enviarCorreoRecuperacion(String destinatario, String nombres, String resetLink);
}
