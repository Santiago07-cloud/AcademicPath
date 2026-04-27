package com.academicpath.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponse {

    private Long id;
    private String nombres;
    private String apellidos;
    private String correo;
    private String universidad;
    private String carrera;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}

