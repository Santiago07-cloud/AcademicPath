package com.academicpath.backend.controller;

import com.academicpath.backend.dto.request.ProfesorRequest;
import com.academicpath.backend.dto.response.ApiResponse;
import com.academicpath.backend.dto.response.ProfesorResponse;
import com.academicpath.backend.service.ProfesorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/profesores")
@Tag(name = "Profesores", description = "Gestión de profesores")
@SecurityRequirement(name = "bearerAuth")
public class ProfesorController {

    @Autowired
    private ProfesorService profesorService;

    @GetMapping
    @Operation(summary = "Obtener todos los profesores")
    public ResponseEntity<ApiResponse<List<ProfesorResponse>>> obtenerTodos() {
        return ResponseEntity.ok(ApiResponse.<List<ProfesorResponse>>builder()
                .success(true)
                .message("Profesores obtenidos exitosamente")
                .data(profesorService.obtenerTodos())
                .build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener profesor por ID")
    public ResponseEntity<ApiResponse<ProfesorResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<ProfesorResponse>builder()
                .success(true)
                .message("Profesor obtenido exitosamente")
                .data(profesorService.obtenerPorId(id))
                .build());
    }

    @PostMapping
    @Operation(summary = "Crear nuevo profesor (solo ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProfesorResponse>> crear(@Valid @RequestBody ProfesorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<ProfesorResponse>builder()
                        .success(true)
                        .message("Profesor creado exitosamente")
                        .data(profesorService.crear(request))
                        .build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar profesor (solo ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProfesorResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProfesorRequest request) {
        return ResponseEntity.ok(ApiResponse.<ProfesorResponse>builder()
                .success(true)
                .message("Profesor actualizado exitosamente")
                .data(profesorService.actualizar(id, request))
                .build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar profesor (solo ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        profesorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
