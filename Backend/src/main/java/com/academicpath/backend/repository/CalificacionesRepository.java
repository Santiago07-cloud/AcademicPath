package com.academicpath.backend.repository;

import com.academicpath.backend.models.entity.Calificaciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalificacionesRepository extends JpaRepository<Calificaciones, Long> {
    List<Calificaciones> findByActividadId(Long actividadId);
}

