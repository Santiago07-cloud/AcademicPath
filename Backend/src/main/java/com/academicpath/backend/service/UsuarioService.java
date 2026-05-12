package com.academicpath.backend.service;

import com.academicpath.backend.dto.request.UpdateUsuarioRequest;
import com.academicpath.backend.dto.response.UsuarioResponse;

import java.util.List;

public interface UsuarioService {

    List<UsuarioResponse> obtenerTodos();

    UsuarioResponse obtenerPorId(Long id);

    UsuarioResponse obtenerPorCorreo(String correo);

    UsuarioResponse actualizar(Long id, UpdateUsuarioRequest request);
}
