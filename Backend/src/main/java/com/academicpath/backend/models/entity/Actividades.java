package com.academicpath.backend.models.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "actividades")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Actividades {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_materia_id", nullable = false)
    @ToString.Exclude
    private UsuarioMaterias usuarioMateria;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private Double peso;

    @Column(nullable = false)
    private Double notaMaxima;

    private LocalDateTime fechaEntrega;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "actividad", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Calificaciones> calificaciones;
}

