package com.academicpath.backend.repository;

import com.academicpath.backend.entity.UsuarioMateria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioMateriaRepository extends JpaRepository<UsuarioMateria, Long> {

    List<UsuarioMateria> findByUsuarioId(Long usuarioId);

    List<UsuarioMateria> findByUsuarioIdAndEstado(Long usuarioId, String estado);

    Optional<UsuarioMateria> findByUsuarioIdAndMateriaId(Long usuarioId, Long materiaId);

    @Query("SELECT um FROM UsuarioMateria um WHERE um.usuario.id = :usuarioId AND um.estado = 'APROBADO'")
    List<UsuarioMateria> findAprobadosByUsuarioId(@Param("usuarioId") Long usuarioId);
}
