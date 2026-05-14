package com.academicpath.backend.mapper;

import com.academicpath.backend.dto.response.UsuarioResponse;
import com.academicpath.backend.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(target = "rol", expression = "java(usuario.getRol() != null ? usuario.getRol().name() : null)")
    UsuarioResponse toResponse(Usuario usuario);

    @Mapping(target = "contrasena", ignore = true)
    @Mapping(target = "usuarioMaterias", ignore = true)
    @Mapping(target = "progresoAcademico", ignore = true)
    @Mapping(target = "sugerenciasMaterias", ignore = true)
    @Mapping(target = "rol", ignore = true)
    Usuario toEntity(UsuarioResponse response);
}
