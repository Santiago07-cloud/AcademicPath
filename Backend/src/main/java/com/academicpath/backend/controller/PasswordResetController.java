package com.academicpath.backend.controller;

import com.academicpath.backend.dto.request.ForgotPasswordRequest;
import com.academicpath.backend.dto.request.ResetPasswordRequest;
import com.academicpath.backend.dto.response.ApiResponse;
import com.academicpath.backend.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Recuperación de contraseña")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    @Operation(summary = "Solicitar recuperación de contraseña por correo")
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        passwordResetService.solicitarRecuperacion(request.getCorreo());

        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Correo de recuperación enviado exitosamente")
                .data("Si el correo existe, recibirás las instrucciones en tu bandeja.")
                .build());
    }

    @GetMapping("/reset-password/validate")
    @Operation(summary = "Validar si un token de recuperación es válido")
    public ResponseEntity<ApiResponse<String>> validateToken(@RequestParam String token) {
        passwordResetService.validarToken(token);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Token válido")
                .data("El enlace de recuperación es válido.")
                .build());
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Restablecer contraseña con token")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        passwordResetService.resetearContrasena(request.getToken(), request.getNuevaContrasena());

        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Contraseña actualizada exitosamente")
                .data("Ya puedes iniciar sesión con tu nueva contraseña.")
                .build());
    }
}

