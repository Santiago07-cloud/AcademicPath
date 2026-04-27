package com.academicpath.backend.mapper;

import com.academicpath.backend.dto.response.UsuarioResponse;
import com.academicpath.backend.models.entity.Usuarios;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuariosMapper {
    UsuarioResponse toResponse(Usuarios usuarios);
    
    @Mapping(target = "contrasena", ignore = true)
    @Mapping(target = "usuarioMaterias", ignore = true)
    @Mapping(target = "progresoAcademico", ignore = true)
    @Mapping(target = "sugerenciasMaterias", ignore = true)
    Usuarios toEntity(UsuarioResponse usuarioResponse);
}

