package com.academicpath.backend.mapper;

import com.academicpath.backend.dto.response.ActividadResponse;
import com.academicpath.backend.entity.Actividad;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ActividadMapper {
    @Mapping(target = "usuarioMateriaId", source = "usuarioMateria.id")
    ActividadResponse toResponse(Actividad actividad);

    @Mapping(target = "usuarioMateria", ignore = true)
    @Mapping(target = "calificaciones", ignore = true)
    Actividad toEntity(ActividadResponse response);
}
