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

    private MateriaInfo materia;
    private String materiaNombre;

    private Integer semestre;
    private Integer anio;
    private String estado;
    private Double notaFinal;
    private Double avancePorcentaje;
    private LocalDateTime fechaCreacion;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MateriaInfo {
        private Long id;
        private String codigo;
        private String nombre;
        private Integer creditos;
        private String descripcion;
    }
}
