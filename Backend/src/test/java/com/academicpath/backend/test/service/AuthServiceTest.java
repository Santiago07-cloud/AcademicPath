package com.academicpath.backend.test.service;

import com.academicpath.backend.dto.request.RegistroRequest;
import com.academicpath.backend.dto.response.UsuarioResponse;
import com.academicpath.backend.entity.Usuario;
import com.academicpath.backend.exception.UsuarioException;
import com.academicpath.backend.mapper.UsuarioMapper;
import com.academicpath.backend.repository.UsuarioRepository;
import com.academicpath.backend.repository.ProgresoAcademicoRepository;
import com.academicpath.backend.service.ProgresoAcademicoService;
import com.academicpath.backend.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ProgresoAcademicoService progresoAcademicoService;

    @Mock
    private ProgresoAcademicoRepository progresoAcademicoRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    public void testRegistroExitoso() {
        RegistroRequest request = RegistroRequest.builder()
                .nombres("Juan")
                .apellidos("Pérez")
                .correo("juan@test.com")
                .contrasena("Password123!")
                .universidad("UdeA")
                .carrera("Ingeniería de Sistemas")
                .build();

        Usuario usuarioGuardado = Usuario.builder()
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

        when(usuarioRepository.existsByCorreo(request.getCorreo())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(usuarioRepository.save(any())).thenReturn(usuarioGuardado);
        when(usuarioMapper.toResponse(usuarioGuardado)).thenReturn(response);
        doNothing().when(progresoAcademicoService).inicializarProgreso(any());

        UsuarioResponse result = authService.registro(request);

        assertNotNull(result);
        assertEquals(request.getCorreo(), result.getCorreo());
        verify(usuarioRepository, times(1)).save(any());
    }

    @Test
    public void testRegistroFallaCorreoExistente() {
        RegistroRequest request = RegistroRequest.builder()
                .nombres("Juan")
                .apellidos("Pérez")
                .correo("juan@test.com")
                .contrasena("Password123!")
                .universidad("UdeA")
                .carrera("Ingeniería de Sistemas")
                .build();

        when(usuarioRepository.existsByCorreo(request.getCorreo())).thenReturn(true);

        assertThrows(UsuarioException.class, () -> authService.registro(request));
    }
}
