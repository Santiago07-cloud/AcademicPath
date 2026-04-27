package com.academicpath.backend.repository;

import com.academicpath.backend.models.entity.Prerrequisitos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrerrequsitosRepository extends JpaRepository<Prerrequisitos, Long> {
    
    @Query("SELECT p.materiaPrerequisito FROM Prerrequisitos p WHERE p.materia.id = :materiaId")
    List<Prerrequisitos> findPrerrequisitosFor(@Param("materiaId") Long materiaId);
    
    List<Prerrequisitos> findByMateriaId(Long materiaId);
}

