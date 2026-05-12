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
public class ProfesorResponse {
    private Long id;
    private String nombre;
    private String correo;
    private LocalDateTime fechaCreacion;
}
