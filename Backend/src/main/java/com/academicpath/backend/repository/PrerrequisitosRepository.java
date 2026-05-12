package com.academicpath.backend.repository;

import com.academicpath.backend.entity.Prerrequisito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrerrequisitosRepository extends JpaRepository<Prerrequisito, Long> {
    List<Prerrequisito> findByMateriaId(Long materiaId);
}
