package com.academicpath.backend.service;

import com.academicpath.backend.dto.response.ProgresoAcademicoResponse;

public interface ProgresoAcademicoService {
    ProgresoAcademicoResponse obtenerProgresoUsuario(Long usuarioId);
    void recalcularProgreso(Long usuarioId);
}

