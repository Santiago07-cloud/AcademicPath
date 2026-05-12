package com.academicpath.backend.service;

import com.academicpath.backend.dto.response.ProgresoAcademicoResponse;
import com.academicpath.backend.entity.Usuario;

public interface ProgresoAcademicoService {
    ProgresoAcademicoResponse obtenerProgresoUsuario(Long usuarioId);
    void recalcularProgreso(Long usuarioId);
    void inicializarProgreso(Usuario usuario);
}
