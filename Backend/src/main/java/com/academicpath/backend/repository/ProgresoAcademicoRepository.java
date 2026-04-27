package com.academicpath.backend.repository;

import com.academicpath.backend.models.entity.ProgresoAcademico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProgresoAcademicoRepository extends JpaRepository<ProgresoAcademico, Long> {
    Optional<ProgresoAcademico> findByUsuarioId(Long usuarioId);
}

