package com.academicpath.backend.controller;

import com.academicpath.backend.dto.request.PrerrequisitoRequest;
import com.academicpath.backend.dto.response.ApiResponse;
import com.academicpath.backend.dto.response.PrerrequisitoResponse;
import com.academicpath.backend.security.SecurityUtils;
import com.academicpath.backend.service.PrerrequisitoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/prerrequisitos")
@Tag(name = "Prerrequisitos", description = "Gestión de prerrequisitos de materias")
@SecurityRequirement(name = "bearerAuth")
public class PrerrequisitoController {

    @Autowired
    private PrerrequisitoService prerrequisitosService;

    @Autowired
    private SecurityUtils securityUtils;

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
    @Operation(summary = "Obtener materias disponibles para el usuario según prerrequisitos")
    public ResponseEntity<ApiResponse<Map<String, Object>>> obtenerMateriasDisponibles(@PathVariable Long usuarioId) {
        securityUtils.validarPropietario(usuarioId);
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
    @Operation(summary = "Verificar si el usuario cumple los prerrequisitos de una materia")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verificarPrerrequisitos(
            @PathVariable Long usuarioId,
            @PathVariable Long materiaId) {
        securityUtils.validarPropietario(usuarioId);
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

    @PostMapping
    @Operation(summary = "Crear prerrequisito de una materia (solo ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PrerrequisitoResponse>> crear(@Valid @RequestBody PrerrequisitoRequest request) {
        PrerrequisitoResponse prerrequisito = prerrequisitosService.crearPrerrequisito(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<PrerrequisitoResponse>builder()
                        .success(true)
                        .message("Prerrequisito creado exitosamente")
                        .data(prerrequisito)
                        .build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar prerrequisito (solo ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        prerrequisitosService.eliminarPrerrequisito(id);
        return ResponseEntity.noContent().build();
    }
}
