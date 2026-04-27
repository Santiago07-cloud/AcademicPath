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
public class MateriaRequest {

    @NotBlank(message = "El código de la materia es requerido")
    @Size(min = 1, max = 20, message = "El código debe tener entre 1 y 20 caracteres")
    private String codigo;

    @NotBlank(message = "El nombre de la materia es requerido")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @NotNull(message = "Los créditos son requeridos")
    @Min(value = 1, message = "Los créditos deben ser mayor a 0")
    @Max(value = 10, message = "Los créditos no pueden ser mayor a 10")
    private Integer creditos;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;
}

