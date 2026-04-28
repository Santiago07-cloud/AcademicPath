package com.academicpath.backend.controller;

import com.academicpath.backend.dto.request.CalificacionRequest;
import com.academicpath.backend.dto.response.ApiResponse;
import com.academicpath.backend.dto.response.CalificacionResponse;
import com.academicpath.backend.service.CalificacionService;
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
@RequestMapping("/calificaciones")
@Tag(name = "Calificaciones", description = "Gestión de calificaciones")
@SecurityRequirement(name = "bearerAuth")
public class CalificacionController {

    @Autowired
    private CalificacionService calificacionService;

    @PostMapping
    @Operation(summary = "Crear nueva calificación")
    public ResponseEntity<ApiResponse<CalificacionResponse>> crear(@Valid @RequestBody CalificacionRequest request) {
        CalificacionResponse calificacion = calificacionService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<CalificacionResponse>builder()
                        .success(true)
                        .message("Calificación creada exitosamente")
                        .data(calificacion)
                        .build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener calificación por ID")
    public ResponseEntity<ApiResponse<CalificacionResponse>> obtenerPorId(@PathVariable Long id) {
        CalificacionResponse calificacion = calificacionService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.<CalificacionResponse>builder()
                .success(true)
                .message("Calificación obtenida exitosamente")
                .data(calificacion)
                .build());
    }

    @GetMapping("/actividad/{actividadId}")
    @Operation(summary = "Obtener calificaciones por actividad")
    public ResponseEntity<ApiResponse<List<CalificacionResponse>>> obtenerPorActividad(
            @PathVariable Long actividadId) {
        List<CalificacionResponse> calificaciones = calificacionService.obtenerPorActividad(actividadId);
        return ResponseEntity.ok(ApiResponse.<List<CalificacionResponse>>builder()
                .success(true)
                .message("Calificaciones obtenidas exitosamente")
                .data(calificaciones)
                .build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar calificación")
    public ResponseEntity<ApiResponse<CalificacionResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CalificacionRequest request) {
        CalificacionResponse calificacion = calificacionService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.<CalificacionResponse>builder()
                .success(true)
                .message("Calificación actualizada exitosamente")
                .data(calificacion)
                .build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar calificación")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        calificacionService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Calificación eliminada exitosamente")
                .build());
    }
}

