package com.academicpath.backend.controller;

import com.academicpath.backend.dto.request.ActividadRequest;
import com.academicpath.backend.dto.response.ActividadResponse;
import com.academicpath.backend.dto.response.ApiResponse;
import com.academicpath.backend.entity.UsuarioMateria;
import com.academicpath.backend.exception.ResourceNotFoundException;
import com.academicpath.backend.repository.UsuarioMateriaRepository;
import com.academicpath.backend.security.SecurityUtils;
import com.academicpath.backend.service.ActividadService;
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
@RequestMapping("/actividades")
@Tag(name = "Actividades", description = "Gestión de actividades académicas")
@SecurityRequirement(name = "bearerAuth")
public class ActividadController {

    @Autowired
    private ActividadService actividadService;

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private UsuarioMateriaRepository usuarioMateriaRepository;

    /**
     * Resuelve el usuarioId dueño de una UsuarioMateria y valida ownership.
     */
    private void validarOwnershipPorUsuarioMateria(Long usuarioMateriaId) {
        UsuarioMateria um = usuarioMateriaRepository.findById(usuarioMateriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario-Materia no encontrada con id: " + usuarioMateriaId));
        securityUtils.validarPropietario(um.getUsuario().getId());
    }

    @PostMapping
    @Operation(summary = "Crear nueva actividad")
    public ResponseEntity<ApiResponse<ActividadResponse>> crear(@Valid @RequestBody ActividadRequest request) {
        validarOwnershipPorUsuarioMateria(request.getUsuarioMateriaId());
        ActividadResponse actividad = actividadService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<ActividadResponse>builder()
                        .success(true)
                        .message("Actividad creada exitosamente")
                        .data(actividad)
                        .build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener actividad por ID")
    public ResponseEntity<ApiResponse<ActividadResponse>> obtenerPorId(@PathVariable Long id) {
        ActividadResponse actividad = actividadService.obtenerPorId(id);
        validarOwnershipPorUsuarioMateria(actividad.getUsuarioMateriaId());
        return ResponseEntity.ok(ApiResponse.<ActividadResponse>builder()
                .success(true)
                .message("Actividad obtenida exitosamente")
                .data(actividad)
                .build());
    }

    @GetMapping("/usuario-materia/{usuarioMateriaId}")
    @Operation(summary = "Obtener actividades por usuario-materia")
    public ResponseEntity<ApiResponse<List<ActividadResponse>>> obtenerPorUsuarioMateria(
            @PathVariable Long usuarioMateriaId) {
        validarOwnershipPorUsuarioMateria(usuarioMateriaId);
        List<ActividadResponse> actividades = actividadService.obtenerPorUsuarioMateria(usuarioMateriaId);
        return ResponseEntity.ok(ApiResponse.<List<ActividadResponse>>builder()
                .success(true)
                .message("Actividades obtenidas exitosamente")
                .data(actividades)
                .build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar actividad")
    public ResponseEntity<ApiResponse<ActividadResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActividadRequest request) {
        ActividadResponse existing = actividadService.obtenerPorId(id);
        validarOwnershipPorUsuarioMateria(existing.getUsuarioMateriaId());
        ActividadResponse actividad = actividadService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.<ActividadResponse>builder()
                .success(true)
                .message("Actividad actualizada exitosamente")
                .data(actividad)
                .build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar actividad")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ActividadResponse existing = actividadService.obtenerPorId(id);
        validarOwnershipPorUsuarioMateria(existing.getUsuarioMateriaId());
        actividadService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
