package com.academicpath.backend.service.impl;

import com.academicpath.backend.entity.PasswordResetToken;
import com.academicpath.backend.entity.Usuario;
import com.academicpath.backend.exception.UsuarioException;
import com.academicpath.backend.repository.PasswordResetTokenRepository;
import com.academicpath.backend.repository.UsuarioRepository;
import com.academicpath.backend.service.EmailService;
import com.academicpath.backend.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    private static final int TOKEN_EXPIRATION_MINUTES = 30;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    @Override
    @Transactional
    public void solicitarRecuperacion(String correo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsuarioException("No existe cuenta con ese correo"));

        // Invalidar tokens previos del usuario
        tokenRepository.invalidarTokensDeUsuario(usuario.getId());

        // Generar token seguro
        String rawToken = generarTokenSeguro();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(rawToken)
                .usuario(usuario)
                .fechaExpiracion(LocalDateTime.now().plusMinutes(TOKEN_EXPIRATION_MINUTES))
                .usado(false)
                .build();

        tokenRepository.save(resetToken);

        // Construir link y enviar correo
        String resetLink = frontendUrl + "/reset-password?token=" + rawToken;
        emailService.enviarCorreoRecuperacion(usuario.getCorreo(), usuario.getNombres(), resetLink);
    }

    @Override
    public void validarToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new UsuarioException("Token inválido o expirado"));

        if (!resetToken.isValid()) {
            throw new UsuarioException("El enlace ha expirado o ya fue utilizado");
        }
    }

    @Override
    @Transactional
    public void resetearContrasena(String token, String nuevaContrasena) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new UsuarioException("Token inválido o expirado"));

        if (!resetToken.isValid()) {
            throw new UsuarioException("El enlace ha expirado o ya fue utilizado");
        }

        // Actualizar contraseña con hash BCrypt
        Usuario usuario = resetToken.getUsuario();
        usuario.setContrasena(passwordEncoder.encode(nuevaContrasena));
        usuarioRepository.save(usuario);

        // Invalidar el token (un solo uso)
        resetToken.setUsado(true);
        tokenRepository.save(resetToken);
    }

    /**
     * Genera un token seguro usando SecureRandom y Base64
     */
    private String generarTokenSeguro() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
