package com.academicpath.backend.models.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "calificaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Calificaciones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "actividad_id", nullable = false)
    @ToString.Exclude
    private Actividades actividad;

    @Column(nullable = false)
    private Double nota;

    private String retroalimentacion;

    @CreationTimestamp
    @Column(name = "fecha_calificacion", nullable = false, updatable = false)
    private LocalDateTime fechaCalificacion;
}

