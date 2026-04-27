package com.academicpath.backend.test.service;

import com.academicpath.backend.dto.request.RegistroRequest;
import com.academicpath.backend.dto.response.UsuarioResponse;
import com.academicpath.backend.exception.UsuarioException;
import com.academicpath.backend.mapper.UsuariosMapper;
import com.academicpath.backend.models.entity.Usuarios;
import com.academicpath.backend.repository.UsuariosRepository;
import com.academicpath.backend.repository.ProgresoAcademicoRepository;
import com.academicpath.backend.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UsuariosRepository usuariosRepository;

    @Mock
    private ProgresoAcademicoRepository progresoAcademicoRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UsuariosMapper usuariosMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    public void testRegistroExitoso() {
        // Arrange
        RegistroRequest request = RegistroRequest.builder()
                .nombres("Juan")
                .apellidos("Pérez")
                .correo("juan@test.com")
                .contrasena("Password123!")
                .universidad("UdeA")
                .carrera("Ingeniería de Sistemas")
                .build();

        Usuarios usuarioGuardado = Usuarios.builder()
                .id(1L)
                .nombres(request.getNombres())
                .apellidos(request.getApellidos())
                .correo(request.getCorreo())
                .universidad(request.getUniversidad())
                .carrera(request.getCarrera())
                .build();

        UsuarioResponse response = UsuarioResponse.builder()
                .id(1L)
                .nombres(request.getNombres())
                .apellidos(request.getApellidos())
                .correo(request.getCorreo())
                .universidad(request.getUniversidad())
                .carrera(request.getCarrera())
                .build();

        when(usuariosRepository.existsByCorreo(request.getCorreo())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(usuariosRepository.save(any())).thenReturn(usuarioGuardado);
        when(usuariosMapper.toResponse(usuarioGuardado)).thenReturn(response);
        when(progresoAcademicoRepository.save(any())).thenReturn(null);

        // Act
        UsuarioResponse result = authService.registro(request);

        // Assert
        assertNotNull(result);
        assertEquals(request.getCorreo(), result.getCorreo());
        verify(usuariosRepository, times(1)).save(any());
    }

    @Test
    public void testRegistroFallaCorreoExistente() {
        // Arrange
        RegistroRequest request = RegistroRequest.builder()
                .nombres("Juan")
                .apellidos("Pérez")
                .correo("juan@test.com")
                .contrasena("Password123!")
                .universidad("UdeA")
                .carrera("Ingeniería de Sistemas")
                .build();

        when(usuariosRepository.existsByCorreo(request.getCorreo())).thenReturn(true);

        // Act & Assert
        assertThrows(UsuarioException.class, () -> authService.registro(request));
    }
}

