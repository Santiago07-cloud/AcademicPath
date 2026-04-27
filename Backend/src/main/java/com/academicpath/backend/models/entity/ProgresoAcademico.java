package com.academicpath.backend.models.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "progreso_academico", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"usuario_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgresoAcademico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    @ToString.Exclude
    private Usuarios usuario;

    @Column(nullable = false)
    private Integer creditosTotales;

    @Column(nullable = false)
    private Integer creditosAprobados;

    @Column(nullable = false)
    private Double promedio;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}

