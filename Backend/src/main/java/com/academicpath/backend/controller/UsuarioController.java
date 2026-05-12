package com.academicpath.backend.controller;

import com.academicpath.backend.dto.request.UpdateUsuarioRequest;
import com.academicpath.backend.dto.response.ApiResponse;
import com.academicpath.backend.dto.response.UsuarioResponse;
import com.academicpath.backend.security.SecurityUtils;
import com.academicpath.backend.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuarios", description = "Gestión de usuarios")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private SecurityUtils securityUtils;

    @GetMapping
    @Operation(summary = "Obtener todos los usuarios (solo ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UsuarioResponse>>> obtenerTodos() {
        return ResponseEntity.ok(ApiResponse.<List<UsuarioResponse>>builder()
                .success(true)
                .message("Usuarios obtenidos exitosamente")
                .data(usuarioService.obtenerTodos())
                .build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID (propio o ADMIN)")
    public ResponseEntity<ApiResponse<UsuarioResponse>> obtenerPorId(@PathVariable Long id) {
        securityUtils.validarPropietario(id);
        return ResponseEntity.ok(ApiResponse.<UsuarioResponse>builder()
                .success(true)
                .message("Usuario obtenido exitosamente")
                .data(usuarioService.obtenerPorId(id))
                .build());
    }

    @GetMapping("/profile")
    @Operation(summary = "Obtener perfil del usuario autenticado")
    public ResponseEntity<ApiResponse<UsuarioResponse>> obtenerPerfil() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(ApiResponse.<UsuarioResponse>builder()
                .success(true)
                .message("Perfil obtenido exitosamente")
                .data(usuarioService.obtenerPorCorreo(authentication.getName()))
                .build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar perfil del usuario (propio o ADMIN)")
    public ResponseEntity<ApiResponse<UsuarioResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUsuarioRequest request) {
        securityUtils.validarPropietario(id);
        return ResponseEntity.ok(ApiResponse.<UsuarioResponse>builder()
                .success(true)
                .message("Usuario actualizado exitosamente")
                .data(usuarioService.actualizar(id, request))
                .build());
    }
}
