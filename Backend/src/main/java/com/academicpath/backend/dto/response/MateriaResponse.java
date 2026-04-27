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
public class MateriaResponse {

    private Long id;
    private String codigo;
    private String nombre;
    private Integer creditos;
    private String descripcion;
    private LocalDateTime fechaCreacion;
}

