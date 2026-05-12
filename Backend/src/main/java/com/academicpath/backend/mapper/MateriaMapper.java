package com.academicpath.backend.mapper;

import com.academicpath.backend.dto.response.MateriaResponse;
import com.academicpath.backend.entity.Materia;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MateriaMapper {
    MateriaResponse toResponse(Materia materia);

    @Mapping(target = "usuarioMaterias", ignore = true)
    @Mapping(target = "prerrequisitos", ignore = true)
    @Mapping(target = "tienePrerrequisitos", ignore = true)
    @Mapping(target = "sugerenciasMaterias", ignore = true)
    Materia toEntity(MateriaResponse response);
}
