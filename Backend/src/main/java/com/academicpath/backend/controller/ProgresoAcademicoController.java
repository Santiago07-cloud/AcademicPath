package com.academicpath.backend.controller;

import com.academicpath.backend.dto.response.ApiResponse;
import com.academicpath.backend.dto.response.ProgresoAcademicoResponse;
import com.academicpath.backend.service.ProgresoAcademicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/progreso")
@Tag(name = "Progreso Académico", description = "Gestión del progreso académico")
@SecurityRequirement(name = "bearerAuth")
public class ProgresoAcademicoController {

    @Autowired
    private ProgresoAcademicoService progresoAcademicoService;

    @GetMapping("/{usuarioId}")
    @Operation(summary = "Obtener progreso académico del usuario")
    public ResponseEntity<ApiResponse<ProgresoAcademicoResponse>> obtenerProgreso(@PathVariable Long usuarioId) {
        ProgresoAcademicoResponse progreso = progresoAcademicoService.obtenerProgresoUsuario(usuarioId);
        return ResponseEntity.ok(ApiResponse.<ProgresoAcademicoResponse>builder()
                .success(true)
                .message("Progreso académico obtenido exitosamente")
                .data(progreso)
                .build());
    }

    @PostMapping("/{usuarioId}/recalcular")
    @Operation(summary = "Recalcular progreso académico del usuario")
    public ResponseEntity<ApiResponse<Void>> recalcularProgreso(@PathVariable Long usuarioId) {
        progresoAcademicoService.recalcularProgreso(usuarioId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Progreso académico recalculado exitosamente")
                .build());
    }
}

