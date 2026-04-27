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
public class ProgresoAcademicoResponse {

    private Long id;
    private Long usuarioId;
    private Integer creditosTotales;
    private Integer creditosAprobados;
    private Double promedio;
    private LocalDateTime fechaActualizacion;
}

