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

<<<<<<< HEAD
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
=======
    @Column(nullable = false, unique = true, length = 500)
    private String token;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDateTime fechaExpiracion;

    @Column(nullable = false)
    @Builder.Default
>>>>>>> 9754ef26dd20d0a8b3bc72447006849f61443a43
    private boolean usado = false;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

<<<<<<< HEAD
    /** Comprueba si el token ha expirado */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.fechaExpiracion);
    }

    /** Comprueba si el token es válido (no usado y no expirado) */
    public boolean isValid() {
        return !this.usado && !this.isExpired();
    }
}
=======
    /**
     * Valida si el token es válido (no expirado y no usado)
     */
    public boolean isValid() {
        return !usado && LocalDateTime.now().isBefore(fechaExpiracion);
    }
}

>>>>>>> 9754ef26dd20d0a8b3bc72447006849f61443a43
