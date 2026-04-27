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
public class CalificacionResponse {

    private Long id;
    private Long actividadId;
    private Double nota;
    private String retroalimentacion;
    private LocalDateTime fechaCalificacion;
}

