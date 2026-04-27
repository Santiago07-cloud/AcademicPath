package com.academicpath.backend.repository;

import com.academicpath.backend.models.entity.SugerenciasMaterias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SugerenciasMateriasRepository extends JpaRepository<SugerenciasMaterias, Long> {
    List<SugerenciasMaterias> findByUsuarioId(Long usuarioId);
    List<SugerenciasMaterias> findByUsuarioIdAndDisponible(Long usuarioId, Boolean disponible);
}

