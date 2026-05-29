package com.academicpath.backend.mapper;

import com.academicpath.backend.dto.response.UsuarioMateriaResponse;
import com.academicpath.backend.entity.Materia;
import com.academicpath.backend.entity.UsuarioMateria;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioMateriaMapper {

    @Mapping(target = "usuarioId",        source = "usuario.id")
    @Mapping(target = "materiaId",        source = "materia.id")
    @Mapping(target = "materiaNombre",    source = "materia.nombre")
    @Mapping(target = "materia",          source = "materia")
    @Mapping(target = "avancePorcentaje", ignore = true)
    UsuarioMateriaResponse toResponse(UsuarioMateria usuarioMateria);

    @Mapping(target = "id",          source = "id")
    @Mapping(target = "codigo",      source = "codigo")
    @Mapping(target = "nombre",      source = "nombre")
    @Mapping(target = "creditos",    source = "creditos")
    @Mapping(target = "descripcion", source = "descripcion")
    UsuarioMateriaResponse.MateriaInfo toMateriaInfo(Materia materia);
}
