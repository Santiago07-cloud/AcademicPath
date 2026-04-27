package com.academicpath.backend.repository;

import com.academicpath.backend.models.entity.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuariosRepository extends JpaRepository<Usuarios, Long> {
    Optional<Usuarios> findByCorreo(String correo);
    boolean existsByCorreo(String correo);
}

