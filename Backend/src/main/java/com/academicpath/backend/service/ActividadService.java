package com.academicpath.backend.service;

import com.academicpath.backend.dto.request.ActividadRequest;
import com.academicpath.backend.dto.response.ActividadResponse;

import java.util.List;

public interface ActividadService {
    ActividadResponse crear(ActividadRequest request);
    ActividadResponse obtenerPorId(Long id);
    List<ActividadResponse> obtenerPorUsuarioMateria(Long usuarioMateriaId);
    ActividadResponse actualizar(Long id, ActividadRequest request);
    void eliminar(Long id);
}

