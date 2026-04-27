package com.academicpath.backend.controller;

import com.academicpath.backend.dto.response.ApiResponse;
import com.academicpath.backend.service.PrerrequisitoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/prerrequisitos")
@Tag(name = "Prerrequisitos", description = "Gestión de prerrequisitos")
@SecurityRequirement(name = "bearerAuth")
public class PrerrequisitoController {

    @Autowired
    private PrerrequisitoService prerrequisitosService;

    @GetMapping("/materia/{materiaId}")
    @Operation(summary = "Obtener prerrequisitos de una materia")
    public ResponseEntity<ApiResponse<Map<String, Object>>> obtenerPrerrequisitos(@PathVariable Long materiaId) {
        Map<String, Object> response = new HashMap<>();
        response.put("materiaId", materiaId);
        response.put("prerequisitos", prerrequisitosService.obtenerPrerrequisitosMateria(materiaId));
        
        return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .success(true)
                .message("Prerrequisitos obtenidos exitosamente")
                .data(response)
                .build());
    }

    @GetMapping("/disponibles/{usuarioId}")
    @Operation(summary = "Obtener materias disponibles según prerrequisitos")
    public ResponseEntity<ApiResponse<Map<String, Object>>> obtenerMateriasDisponibles(@PathVariable Long usuarioId) {
        Set<Long> materiasDisponibles = prerrequisitosService.calcularMateriasDisponibles(usuarioId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("usuarioId", usuarioId);
        response.put("materiasDisponibles", materiasDisponibles);
        
        return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .success(true)
                .message("Materias disponibles obtenidas exitosamente")
                .data(response)
                .build());
    }

    @GetMapping("/verificar/{usuarioId}/{materiaId}")
    @Operation(summary = "Verificar si usuario cumple prerrequisitos de materia")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verificarPrerrequisitos(
            @PathVariable Long usuarioId,
            @PathVariable Long materiaId) {
        boolean cumple = prerrequisitosService.verificarPrerrequisitosCompletos(usuarioId, materiaId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("usuarioId", usuarioId);
        response.put("materiaId", materiaId);
        response.put("cumplePrerrequisitos", cumple);
        
        return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .success(true)
                .message("Verificación completada")
                .data(response)
                .build());
    }
}

