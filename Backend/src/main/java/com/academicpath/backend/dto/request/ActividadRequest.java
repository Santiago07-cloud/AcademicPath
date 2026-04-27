package com.academicpath.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActividadRequest {

    @NotNull(message = "El ID de usuario-materia es requerido")
    private Long usuarioMateriaId;

    @NotBlank(message = "El título de la actividad es requerido")
    @Size(min = 2, max = 100, message = "El título debe tener entre 2 y 100 caracteres")
    private String titulo;

    @NotBlank(message = "El tipo de actividad es requerido")
    @Size(min = 2, max = 50, message = "El tipo debe tener entre 2 y 50 caracteres")
    private String tipo;

    @NotNull(message = "El peso es requerido")
    @DecimalMin(value = "0.1", message = "El peso debe ser mayor a 0")
    @DecimalMax(value = "100.0", message = "El peso no puede exceder 100")
    private Double peso;

    @NotNull(message = "La nota máxima es requerida")
    @DecimalMin(value = "0.1", message = "La nota máxima debe ser mayor a 0")
    private Double notaMaxima;

    private LocalDateTime fechaEntrega;
}

