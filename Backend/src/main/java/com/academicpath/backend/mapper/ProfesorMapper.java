package com.academicpath.backend.mapper;

import com.academicpath.backend.dto.response.ProfesorResponse;
import com.academicpath.backend.entity.Profesor;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfesorMapper {

    ProfesorResponse toResponse(Profesor profesor);
}
