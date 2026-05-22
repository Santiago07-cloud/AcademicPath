package com.academicpath.backend.service;

<<<<<<< HEAD
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
=======
public interface EmailService {
    /**
     * Envía un correo de recuperación de contraseña
     * @param destinatario Correo del usuario
     * @param nombres Nombres del usuario
     * @param resetLink Link para restaurar la contraseña
     */
    void enviarCorreoRecuperacion(String destinatario, String nombres, String resetLink);
}

>>>>>>> 9754ef26dd20d0a8b3bc72447006849f61443a43
