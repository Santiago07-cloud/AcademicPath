package com.academicpath.backend.service.impl;

import com.academicpath.backend.dto.request.RegistroRequest;
import com.academicpath.backend.dto.request.LoginRequest;
import com.academicpath.backend.dto.response.UsuarioResponse;
import com.academicpath.backend.dto.response.LoginResponse;
import com.academicpath.backend.exception.UsuarioException;
import com.academicpath.backend.mapper.UsuariosMapper;
import com.academicpath.backend.models.entity.Usuarios;
import com.academicpath.backend.models.entity.ProgresoAcademico;
import com.academicpath.backend.repository.UsuariosRepository;
import com.academicpath.backend.repository.ProgresoAcademicoRepository;
import com.academicpath.backend.security.JwtUtil;
import com.academicpath.backend.security.UsuarioUserDetails;
import com.academicpath.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UsuariosRepository usuariosRepository;

    @Autowired
    private ProgresoAcademicoRepository progresoAcademicoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuariosMapper usuariosMapper;

    @Override
    @Transactional
    public UsuarioResponse registro(RegistroRequest request) {
        if (usuariosRepository.existsByCorreo(request.getCorreo())) {
            throw new UsuarioException("El correo ya está registrado: " + request.getCorreo());
        }

        Usuarios usuario = Usuarios.builder()
                .nombres(request.getNombres())
                .apellidos(request.getApellidos())
                .correo(request.getCorreo())
                .contrasena(passwordEncoder.encode(request.getContrasena()))
                .universidad(request.getUniversidad())
                .carrera(request.getCarrera())
                .build();

        Usuarios usuarioGuardado = usuariosRepository.save(usuario);

        ProgresoAcademico progreso = ProgresoAcademico.builder()
                .usuario(usuarioGuardado)
                .creditosTotales(0)
                .creditosAprobados(0)
                .promedio(0.0)
                .fechaActualizacion(LocalDateTime.now())
                .build();

        progresoAcademicoRepository.save(progreso);

        return usuariosMapper.toResponse(usuarioGuardado);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getCorreo(), request.getContrasena())
            );

            UsuarioUserDetails userDetails = (UsuarioUserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails, userDetails.getId());

            return LoginResponse.builder()
                    .accessToken(token)
                    .tokenType("Bearer")
                    .expiresIn(jwtUtil.getExpirationTime())
                    .usuario(usuariosMapper.toResponse(userDetails.getUsuario()))
                    .build();
        } catch (AuthenticationException ex) {
            throw new UsuarioException("Credenciales inválidas");
        }
    }
}

