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

    // Objeto materia completo para que el frontend pueda mostrar código y créditos
    private MateriaInfo materia;

    // Mantener campo plano para compatibilidad
    private String materiaNombre;

    private Long profesorId;
    private String profesorNombre;
    private Integer semestre;
    private Integer anio;
    private String estado;
    private Double notaFinal;
    private Double avancePorcentaje; // 0.0 a 100.0 — porcentaje de actividades calificadas
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
