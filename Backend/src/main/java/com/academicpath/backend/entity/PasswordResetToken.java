package com.academicpath.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Token UUID seguro generado con SecureRandom */
    @Column(nullable = false, unique = true)
    private String token;

    /** Usuario propietario del token */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /** Fecha/hora de expiración: 30 minutos desde la creación */
    @Column(name = "fecha_expiracion", nullable = false)
    private LocalDateTime fechaExpiracion;

    /** Indica si ya fue utilizado (un solo uso) */
    @Builder.Default
    @Column(nullable = false)
    private boolean usado = false;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    /** Comprueba si el token ha expirado */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.fechaExpiracion);
    }

    /** Comprueba si el token es válido (no usado y no expirado) */
    public boolean isValid() {
        return !this.usado && !this.isExpired();
    }
}
