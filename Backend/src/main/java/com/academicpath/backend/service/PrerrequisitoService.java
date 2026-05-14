package com.academicpath.backend.service;

import com.academicpath.backend.dto.request.PrerrequisitoRequest;
import com.academicpath.backend.dto.response.PrerrequisitoResponse;

import java.util.List;
import java.util.Set;

public interface PrerrequisitoService {

    /** Retorna solo los IDs de las materias que son prerrequisito (uso interno). */
    List<Long> obtenerPrerrequisitosMateria(Long materiaId);

    /** Retorna los objetos PrerrequisitoResponse completos para la vista del Admin. */
    List<PrerrequisitoResponse> obtenerPrerrequisitosCompletosMateria(Long materiaId);

    Set<Long> calcularMateriasDisponibles(Long usuarioId);

    boolean verificarPrerrequisitosCompletos(Long usuarioId, Long materiaId);

    PrerrequisitoResponse crearPrerrequisito(PrerrequisitoRequest request);

    void eliminarPrerrequisito(Long prerrequisitId);
}
