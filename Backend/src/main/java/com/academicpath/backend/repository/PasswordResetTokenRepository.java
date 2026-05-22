package com.academicpath.backend.repository;

import com.academicpath.backend.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    /**
     * Busca un token de recuperación por su valor
     */
    Optional<PasswordResetToken> findByToken(String token);

    /**
     * Invalida todos los tokens pendientes de un usuario
     */
    @Modifying
    @Transactional
    @Query("UPDATE PasswordResetToken p SET p.usado = true WHERE p.usuario.id = :usuarioId AND p.usado = false")
    void invalidarTokensDeUsuario(@Param("usuarioId") Long usuarioId);

    /**
     * Elimina tokens expirados
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetToken p WHERE p.fechaExpiracion < CURRENT_TIMESTAMP")
    void eliminarTokensExpirados();
}

