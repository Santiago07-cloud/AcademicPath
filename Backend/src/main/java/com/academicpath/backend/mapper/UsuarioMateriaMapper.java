package com.academicpath.backend.mapper;

import com.academicpath.backend.dto.response.UsuarioMateriaResponse;
import com.academicpath.backend.entity.UsuarioMateria;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioMateriaMapper {

    @Mapping(target = "usuarioId", source = "usuario.id")
    @Mapping(target = "materiaId", source = "materia.id")
    @Mapping(target = "materiaNombre", source = "materia.nombre")
    @Mapping(target = "profesorId", source = "profesor.id")
    @Mapping(target = "profesorNombre", source = "profesor.nombre")
    @Mapping(target = "materia", source = "materia")
    UsuarioMateriaResponse toResponse(UsuarioMateria usuarioMateria);

    @Mapping(target = "id", source = "materia.id")
    @Mapping(target = "codigo", source = "materia.codigo")
    @Mapping(target = "nombre", source = "materia.nombre")
    @Mapping(target = "creditos", source = "materia.creditos")
    @Mapping(target = "descripcion", source = "materia.descripcion")
    UsuarioMateriaResponse.MateriaInfo toMateriaInfo(UsuarioMateria usuarioMateria);
}
