package com.academicpath.backend.repository;

import com.academicpath.backend.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
<<<<<<< HEAD
=======
import org.springframework.data.repository.query.Param;
>>>>>>> 9754ef26dd20d0a8b3bc72447006849f61443a43
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

<<<<<<< HEAD
    Optional<PasswordResetToken> findByToken(String token);

    /** Invalida todos los tokens previos de un usuario antes de emitir uno nuevo */
    @Modifying
    @Transactional
    @Query("UPDATE PasswordResetToken t SET t.usado = true WHERE t.usuario.id = :usuarioId AND t.usado = false")
    void invalidarTokensDeUsuario(Long usuarioId);

    /** Limpieza periódica de tokens expirados (puede ejecutarse con @Scheduled) */
    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetToken t WHERE t.fechaExpiracion < :ahora")
    void eliminarTokensExpirados(LocalDateTime ahora);
}
=======
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

>>>>>>> 9754ef26dd20d0a8b3bc72447006849f61443a43
