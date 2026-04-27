package com.academicpath.backend.controller;

import com.academicpath.backend.dto.request.MateriaRequest;
import com.academicpath.backend.dto.response.ApiResponse;
import com.academicpath.backend.dto.response.MateriaResponse;
import com.academicpath.backend.service.MateriaService;
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
@RequestMapping("/api/materias")
@Tag(name = "Materias", description = "Gestión de materias")
@SecurityRequirement(name = "bearerAuth")
public class MateriaController {

    @Autowired
    private MateriaService materiaService;

    @PostMapping
    @Operation(summary = "Crear nueva materia")
    public ResponseEntity<ApiResponse<MateriaResponse>> crear(@Valid @RequestBody MateriaRequest request) {
        MateriaResponse materia = materiaService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<MateriaResponse>builder()
                        .success(true)
                        .message("Materia creada exitosamente")
                        .data(materia)
                        .build());
    }

    @GetMapping
    @Operation(summary = "Obtener todas las materias")
    public ResponseEntity<ApiResponse<List<MateriaResponse>>> obtenerTodas() {
        List<MateriaResponse> materias = materiaService.obtenerTodas();
        return ResponseEntity.ok(ApiResponse.<List<MateriaResponse>>builder()
                .success(true)
                .message("Materias obtenidas exitosamente")
                .data(materias)
                .build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener materia por ID")
    public ResponseEntity<ApiResponse<MateriaResponse>> obtenerPorId(@PathVariable Long id) {
        MateriaResponse materia = materiaService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.<MateriaResponse>builder()
                .success(true)
                .message("Materia obtenida exitosamente")
                .data(materia)
                .build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar materia")
    public ResponseEntity<ApiResponse<MateriaResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody MateriaRequest request) {
        MateriaResponse materia = materiaService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.<MateriaResponse>builder()
                .success(true)
                .message("Materia actualizada exitosamente")
                .data(materia)
                .build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar materia")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        materiaService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Materia eliminada exitosamente")
                .build());
    }
}

