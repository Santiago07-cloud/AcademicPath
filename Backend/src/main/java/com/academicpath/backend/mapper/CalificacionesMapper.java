package com.academicpath.backend.mapper;

import com.academicpath.backend.dto.response.CalificacionResponse;
import com.academicpath.backend.models.entity.Calificaciones;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CalificacionesMapper {
    @Mapping(target = "actividadId", source = "actividad.id")
    CalificacionResponse toResponse(Calificaciones calificacion);
    
    @Mapping(target = "actividad", ignore = true)
    Calificaciones toEntity(CalificacionResponse calificacionResponse);
}

