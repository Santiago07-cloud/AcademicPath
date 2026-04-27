package com.academicpath.backend.controller;

import com.academicpath.backend.dto.request.UsuarioMateriaRequest;
import com.academicpath.backend.dto.response.ApiResponse;
import com.academicpath.backend.dto.response.UsuarioMateriaResponse;
import com.academicpath.backend.service.UsuarioMateriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuario-materias")
@Tag(name = "Usuario Materias", description = "Gestión de inscripción de materias")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioMateriaController {

    @Autowired
    private UsuarioMateriaService usuarioMateriaService;

    @PostMapping
    @Operation(summary = "Inscribir usuario en materia")
    public ResponseEntity<ApiResponse<UsuarioMateriaResponse>> inscribir(@Valid @RequestBody UsuarioMateriaRequest request) {
        UsuarioMateriaResponse usuarioMateria = usuarioMateriaService.inscribir(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<UsuarioMateriaResponse>builder()
                        .success(true)
                        .message("Usuario inscrito en materia exitosamente")
                        .data(usuarioMateria)
                        .build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario-materia por ID")
    public ResponseEntity<ApiResponse<UsuarioMateriaResponse>> obtenerPorId(@PathVariable Long id) {
        UsuarioMateriaResponse usuarioMateria = usuarioMateriaService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.<UsuarioMateriaResponse>builder()
                .success(true)
                .message("Usuario-Materia obtenida exitosamente")
                .data(usuarioMateria)
                .build());
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Obtener materias de un usuario")
    public ResponseEntity<ApiResponse<List<UsuarioMateriaResponse>>> obtenerPorUsuario(@PathVariable Long usuarioId) {
        List<UsuarioMateriaResponse> usuarioMaterias = usuarioMateriaService.obtenerPorUsuario(usuarioId);
        return ResponseEntity.ok(ApiResponse.<List<UsuarioMateriaResponse>>builder()
                .success(true)
                .message("Materias del usuario obtenidas exitosamente")
                .data(usuarioMaterias)
                .build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario-materia")
    public ResponseEntity<ApiResponse<UsuarioMateriaResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioMateriaRequest request) {
        UsuarioMateriaResponse usuarioMateria = usuarioMateriaService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.<UsuarioMateriaResponse>builder()
                .success(true)
                .message("Usuario-Materia actualizada exitosamente")
                .data(usuarioMateria)
                .build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario-materia")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        usuarioMateriaService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Usuario-Materia eliminada exitosamente")
                .build());
    }
}

