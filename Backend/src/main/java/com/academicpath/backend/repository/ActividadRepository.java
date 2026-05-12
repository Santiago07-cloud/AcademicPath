package com.academicpath.backend.repository;

import com.academicpath.backend.entity.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActividadRepository extends JpaRepository<Actividad, Long> {
    List<Actividad> findByUsuarioMateriaId(Long usuarioMateriaId);
}
