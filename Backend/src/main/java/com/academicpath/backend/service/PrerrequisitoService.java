package com.academicpath.backend.service;

import com.academicpath.backend.dto.request.PrerrequisitoRequest;
import com.academicpath.backend.dto.response.PrerrequisitoResponse;

import java.util.List;
import java.util.Set;

public interface PrerrequisitoService {

    List<Long> obtenerPrerrequisitosMateria(Long materiaId);

    Set<Long> calcularMateriasDisponibles(Long usuarioId);

    boolean verificarPrerrequisitosCompletos(Long usuarioId, Long materiaId);

    PrerrequisitoResponse crearPrerrequisito(PrerrequisitoRequest request);

    void eliminarPrerrequisito(Long prerrequisitId);
}
