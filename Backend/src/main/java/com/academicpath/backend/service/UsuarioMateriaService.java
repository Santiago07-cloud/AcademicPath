package com.academicpath.backend.service;

import com.academicpath.backend.dto.request.UsuarioMateriaRequest;
import com.academicpath.backend.dto.response.UsuarioMateriaResponse;

import java.util.List;

public interface UsuarioMateriaService {
    UsuarioMateriaResponse inscribir(UsuarioMateriaRequest request);
    UsuarioMateriaResponse obtenerPorId(Long id);
    List<UsuarioMateriaResponse> obtenerPorUsuario(Long usuarioId);
    UsuarioMateriaResponse actualizar(Long id, UsuarioMateriaRequest request);
    void eliminar(Long id);
}

