package com.academicpath.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrerrequisitoResponse {
    private Long id;
    private Long materiaId;
    private String materiaNombre;
    private Long materiaPrerrequisitId;
    private String materiaPrerrequisitNombre;
    private LocalDateTime fechaCreacion;
}
