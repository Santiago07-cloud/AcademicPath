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
public class UsuarioMateriaResponse {

    private Long id;
    private Long usuarioId;
    private Long materiaId;
    private String materiaNombre;
    private Long profesorId;
    private String profesorNombre;
    private Integer semestre;
    private Integer anio;
    private String estado;
    private Double notaFinal;
    private LocalDateTime fechaCreacion;
}

