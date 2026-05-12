package com.academicpath.backend.service.impl;

import com.academicpath.backend.dto.request.LoginRequest;
import com.academicpath.backend.dto.request.RegistroRequest;
import com.academicpath.backend.dto.response.LoginResponse;
import com.academicpath.backend.dto.response.UsuarioResponse;
import com.academicpath.backend.entity.Usuario;
import com.academicpath.backend.exception.UsuarioException;
import com.academicpath.backend.mapper.UsuarioMapper;
import com.academicpath.backend.repository.UsuarioRepository;
import com.academicpath.backend.security.JwtUtil;
import com.academicpath.backend.security.UsuarioUserDetails;
import com.academicpath.backend.service.AuthService;
import com.academicpath.backend.service.ProgresoAcademicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProgresoAcademicoService progresoAcademicoService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Override
    @Transactional
    public UsuarioResponse registro(RegistroRequest request) {
        if (usuarioRepository.existsByCorreo(request.getCorreo())) {
            throw new UsuarioException("El correo ya está registrado: " + request.getCorreo());
        }

        Usuario usuario = Usuario.builder()
                .nombres(request.getNombres())
                .apellidos(request.getApellidos())
                .correo(request.getCorreo())
                .contrasena(passwordEncoder.encode(request.getContrasena()))
                .universidad(request.getUniversidad())
                .carrera(request.getCarrera())
                .build();

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        progresoAcademicoService.inicializarProgreso(usuarioGuardado);

        return usuarioMapper.toResponse(usuarioGuardado);
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
                    .usuario(usuarioMapper.toResponse(userDetails.getUsuario()))
                    .build();
        } catch (AuthenticationException ex) {
            throw new UsuarioException("Credenciales inválidas");
        }
    }
}
