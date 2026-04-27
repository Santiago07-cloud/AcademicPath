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
public class ActividadResponse {

    private Long id;
    private Long usuarioMateriaId;
    private String titulo;
    private String tipo;
    private Double peso;
    private Double notaMaxima;
    private LocalDateTime fechaEntrega;
    private LocalDateTime fechaCreacion;
}

