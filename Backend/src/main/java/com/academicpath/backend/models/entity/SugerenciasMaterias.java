package com.academicpath.backend.models.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "sugerencias_materias", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"usuario_id", "materia_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SugerenciasMaterias {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    @ToString.Exclude
    private Usuarios usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "materia_id", nullable = false)
    @ToString.Exclude
    private Materias materia;

    @Column(nullable = false)
    private Boolean disponible;

    @CreationTimestamp
    @Column(name = "fecha_generacion", nullable = false, updatable = false)
    private LocalDateTime fechaGeneracion;
}

