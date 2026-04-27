package com.academicpath.backend.repository;

import com.academicpath.backend.models.entity.Profesores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfesoresRepository extends JpaRepository<Profesores, Long> {
    Optional<Profesores> findByCorreo(String correo);
}

