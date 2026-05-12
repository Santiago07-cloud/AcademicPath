package com.academicpath.backend.mapper;

import com.academicpath.backend.dto.response.CalificacionResponse;
import com.academicpath.backend.entity.Calificacion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CalificacionMapper {
    @Mapping(target = "actividadId", source = "actividad.id")
    CalificacionResponse toResponse(Calificacion calificacion);

    @Mapping(target = "actividad", ignore = true)
    Calificacion toEntity(CalificacionResponse response);
}
