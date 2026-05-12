package com.academicpath.backend.service;

import com.academicpath.backend.dto.request.ProfesorRequest;
import com.academicpath.backend.dto.response.ProfesorResponse;

import java.util.List;

public interface ProfesorService {

    List<ProfesorResponse> obtenerTodos();

    ProfesorResponse obtenerPorId(Long id);

    ProfesorResponse crear(ProfesorRequest request);

    ProfesorResponse actualizar(Long id, ProfesorRequest request);

    void eliminar(Long id);
}
