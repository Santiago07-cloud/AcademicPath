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
<<<<<<< HEAD
@Tag(name = "Recuperación de contraseña", description = "Flujo forgot/reset password")
=======
@Tag(name = "Recuperación de contraseña")
>>>>>>> 9754ef26dd20d0a8b3bc72447006849f61443a43
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

<<<<<<< HEAD
    /**
     * POST /api/auth/forgot-password
     * Recibe el correo y dispara el envío del email con el token.
     * Siempre responde con 200 para no filtrar si el correo existe (seguridad).
     */
    @PostMapping("/forgot-password")
    @Operation(summary = "Solicitar recuperación de contraseña")
=======
    @PostMapping("/forgot-password")
    @Operation(summary = "Solicitar recuperación de contraseña por correo")
>>>>>>> 9754ef26dd20d0a8b3bc72447006849f61443a43
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        passwordResetService.solicitarRecuperacion(request.getCorreo());

<<<<<<< HEAD
        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Solicitud procesada")
                        .data("Si el correo está registrado, recibirás las instrucciones en tu bandeja.")
                        .build()
        );
    }

    /**
     * GET /api/auth/reset-password/validate?token=xxx
     * Valida si el token existe y no ha expirado ni fue usado.
     * Usado por el frontend al cargar la vista /reset-password.
     */
    @GetMapping("/reset-password/validate")
    @Operation(summary = "Validar token de recuperación")
    public ResponseEntity<ApiResponse<String>> validateToken(@RequestParam String token) {
        passwordResetService.validarToken(token);
        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Token válido")
                        .data("El enlace de recuperación es válido.")
                        .build()
        );
    }

    /**
     * POST /api/auth/reset-password
     * Cambia la contraseña del usuario usando el token recibido por correo.
     */
=======
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

>>>>>>> 9754ef26dd20d0a8b3bc72447006849f61443a43
    @PostMapping("/reset-password")
    @Operation(summary = "Restablecer contraseña con token")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        passwordResetService.resetearContrasena(request.getToken(), request.getNuevaContrasena());

<<<<<<< HEAD
        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Contraseña actualizada exitosamente")
                        .data("Ya puedes iniciar sesión con tu nueva contraseña.")
                        .build()
        );
    }
}
=======
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Contraseña actualizada exitosamente")
                .data("Ya puedes iniciar sesión con tu nueva contraseña.")
                .build());
    }
}

>>>>>>> 9754ef26dd20d0a8b3bc72447006849f61443a43
