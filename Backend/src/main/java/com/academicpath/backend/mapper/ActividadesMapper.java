package com.academicpath.backend.mapper;

import com.academicpath.backend.dto.response.ActividadResponse;
import com.academicpath.backend.models.entity.Actividades;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ActividadesMapper {
    @Mapping(target = "usuarioMateriaId", source = "usuarioMateria.id")
    ActividadResponse toResponse(Actividades actividad);
    
    @Mapping(target = "usuarioMateria", ignore = true)
    @Mapping(target = "calificaciones", ignore = true)
    Actividades toEntity(ActividadResponse actividadResponse);
}

