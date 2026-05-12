package com.academicpath.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUsuarioRequest {

    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String nombres;

    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    private String apellidos;

    @Size(max = 100, message = "La universidad no puede superar 100 caracteres")
    private String universidad;

    @Size(max = 100, message = "La carrera no puede superar 100 caracteres")
    private String carrera;
}
