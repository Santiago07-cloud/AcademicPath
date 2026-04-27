package com.academicpath.backend.service;

import java.util.List;
import java.util.Set;

public interface PrerrequisitoService {
    List<Long> obtenerPrerrequisitosMateria(Long materiaId);
    Set<Long> calcularMateriasDisponibles(Long usuarioId);
    boolean verificarPrerrequisitosCompletos(Long usuarioId, Long materiaId);
}

