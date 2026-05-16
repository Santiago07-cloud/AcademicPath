package com.academicpath.backend.repository;

import com.academicpath.backend.entity.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActividadRepository extends JpaRepository<Actividad, Long> {
    List<Actividad> findByUsuarioMateriaId(Long usuarioMateriaId);

    /**
     * Devuelve el usuarioId dueño de una Actividad directamente desde la BD,
     * sin necesidad de navegar relaciones lazy en memoria.
     */
    @Query("SELECT a.usuarioMateria.usuario.id FROM Actividad a WHERE a.id = :actividadId")
    Optional<Long> findUsuarioIdByActividadId(@Param("actividadId") Long actividadId);
}
