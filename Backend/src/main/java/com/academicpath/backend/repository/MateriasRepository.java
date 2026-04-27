package com.academicpath.backend.repository;

import com.academicpath.backend.models.entity.Materias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MateriasRepository extends JpaRepository<Materias, Long> {
    Optional<Materias> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
}

