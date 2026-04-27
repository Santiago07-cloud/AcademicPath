package com.academicpath.backend.repository;

import com.academicpath.backend.models.entity.Actividades;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActividadesRepository extends JpaRepository<Actividades, Long> {
    List<Actividades> findByUsuarioMateriaId(Long usuarioMateriaId);
}

