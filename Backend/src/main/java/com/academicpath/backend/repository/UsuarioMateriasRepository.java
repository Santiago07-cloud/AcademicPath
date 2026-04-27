package com.academicpath.backend.repository;

import com.academicpath.backend.models.entity.UsuarioMaterias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioMateriasRepository extends JpaRepository<UsuarioMaterias, Long> {
    
    List<UsuarioMaterias> findByUsuarioId(Long usuarioId);
    
    List<UsuarioMaterias> findByUsuarioIdAndEstado(Long usuarioId, String estado);
    
    Optional<UsuarioMaterias> findByUsuarioIdAndMateriaId(Long usuarioId, Long materiaId);
    
    @Query("SELECT um FROM UsuarioMaterias um WHERE um.usuario.id = :usuarioId AND um.estado = 'APROBADO'")
    List<UsuarioMaterias> findAprobadosByUsuarioId(@Param("usuarioId") Long usuarioId);
}

