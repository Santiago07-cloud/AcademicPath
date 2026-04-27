package com.academicpath.backend.controller;

import com.academicpath.backend.dto.request.ActividadRequest;
import com.academicpath.backend.dto.response.ActividadResponse;
import com.academicpath.backend.dto.response.ApiResponse;
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
@RequestMapping("/api/actividades")
@Tag(name = "Actividades", description = "Gestión de actividades académicas")
@SecurityRequirement(name = "bearerAuth")
public class ActividadController {

    @Autowired
    private ActividadService actividadService;

    @PostMapping
    @Operation(summary = "Crear nueva actividad")
    public ResponseEntity<ApiResponse<ActividadResponse>> crear(@Valid @RequestBody ActividadRequest request) {
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
        ActividadResponse actividad = actividadService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.<ActividadResponse>builder()
                .success(true)
                .message("Actividad actualizada exitosamente")
                .data(actividad)
                .build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar actividad")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        actividadService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Actividad eliminada exitosamente")
                .build());
    }
}

