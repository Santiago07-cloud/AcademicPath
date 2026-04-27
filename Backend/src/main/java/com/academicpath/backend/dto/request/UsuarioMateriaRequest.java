package com.academicpath.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioMateriaRequest {

    @NotNull(message = "El ID del usuario es requerido")
    private Long usuarioId;

    @NotNull(message = "El ID de la materia es requerido")
    private Long materiaId;

    @NotNull(message = "El ID del profesor es requerido")
    private Long profesorId;

    @NotNull(message = "El semestre es requerido")
    private Integer semestre;

    @NotNull(message = "El año es requerido")
    private Integer anio;
}

