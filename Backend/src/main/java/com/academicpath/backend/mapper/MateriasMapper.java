package com.academicpath.backend.mapper;

import com.academicpath.backend.dto.response.MateriaResponse;
import com.academicpath.backend.models.entity.Materias;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MateriasMapper {
    MateriaResponse toResponse(Materias materia);
    
    @Mapping(target = "usuarioMaterias", ignore = true)
    @Mapping(target = "prerrequisitos", ignore = true)
    @Mapping(target = "tienePrerrequisitos", ignore = true)
    @Mapping(target = "sugerenciasMaterias", ignore = true)
    Materias toEntity(MateriaResponse materiaResponse);
}

