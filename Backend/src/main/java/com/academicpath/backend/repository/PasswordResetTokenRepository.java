package com.academicpath.backend.repository;

import com.academicpath.backend.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

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
