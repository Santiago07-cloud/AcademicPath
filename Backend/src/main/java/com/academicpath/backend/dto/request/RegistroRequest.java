package com.academicpath.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroRequest {

    @NotBlank(message = "El nombre es requerido")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String nombres;

    @NotBlank(message = "El apellido es requerido")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    private String apellidos;

    @NotBlank(message = "El correo es requerido")
    @Email(message = "El correo debe ser válido")
    private String correo;

    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).*$", 
             message = "La contraseña debe contener al menos una mayúscula, una minúscula y un dígito")
    private String contrasena;

    @NotBlank(message = "La universidad es requerida")
    @Size(min = 2, max = 100, message = "La universidad debe tener entre 2 y 100 caracteres")
    private String universidad;

    @NotBlank(message = "La carrera es requerida")
    @Size(min = 2, max = 100, message = "La carrera debe tener entre 2 y 100 caracteres")
    private String carrera;
}

