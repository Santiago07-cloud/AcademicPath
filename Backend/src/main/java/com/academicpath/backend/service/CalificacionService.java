package com.academicpath.backend.service;

import com.academicpath.backend.dto.request.CalificacionRequest;
import com.academicpath.backend.dto.response.CalificacionResponse;

import java.util.List;

public interface CalificacionService {
    CalificacionResponse crear(CalificacionRequest request);
    CalificacionResponse obtenerPorId(Long id);
    List<CalificacionResponse> obtenerPorActividad(Long actividadId);
    CalificacionResponse actualizar(Long id, CalificacionRequest request);
    void eliminar(Long id);
}

