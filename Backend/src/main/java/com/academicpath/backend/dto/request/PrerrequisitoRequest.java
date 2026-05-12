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
public class PrerrequisitoRequest {

    @NotNull(message = "El ID de la materia es requerido")
    private Long materiaId;

    @NotNull(message = "El ID de la materia prerrequisito es requerido")
    private Long materiaPrerrequisitId;
}
