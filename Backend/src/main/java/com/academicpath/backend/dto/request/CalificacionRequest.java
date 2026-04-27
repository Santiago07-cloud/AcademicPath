package com.academicpath.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalificacionRequest {

    @NotNull(message = "El ID de actividad es requerido")
    private Long actividadId;

    @NotNull(message = "La nota es requerida")
    @DecimalMin(value = "0", message = "La nota no puede ser negativa")
    @DecimalMax(value = "100", message = "La nota no puede exceder 100")
    private Double nota;

    @Size(max = 500, message = "La retroalimentación no puede exceder 500 caracteres")
    private String retroalimentacion;
}

