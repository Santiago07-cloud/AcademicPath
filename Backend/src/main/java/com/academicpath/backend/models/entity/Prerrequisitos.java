package com.academicpath.backend.models.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "prerrequisitos", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"materia_id", "materia_prerrequisito_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prerrequisitos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "materia_id", nullable = false)
    @ToString.Exclude
    private Materias materia;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "materia_prerrequisito_id", nullable = false)
    @ToString.Exclude
    private Materias materiaPrerequisito;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
}

