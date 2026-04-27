package com.academicpath.backend.service;

import com.academicpath.backend.dto.request.MateriaRequest;
import com.academicpath.backend.dto.response.MateriaResponse;

import java.util.List;

public interface MateriaService {
    MateriaResponse crear(MateriaRequest request);
    MateriaResponse obtenerPorId(Long id);
    List<MateriaResponse> obtenerTodas();
    MateriaResponse actualizar(Long id, MateriaRequest request);
    void eliminar(Long id);
}

