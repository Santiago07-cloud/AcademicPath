package com.academicpath.backend.models.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "usuario_materias", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"usuario_id", "materia_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioMaterias {

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profesor_id")
    @ToString.Exclude
    private Profesores profesor;

    @Column(nullable = false)
    private Integer semestre;

    @Column(nullable = false)
    private Integer anio;

    @Column(nullable = false)
    private String estado;

    private Double notaFinal;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "usuarioMateria", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Actividades> actividades;
}

