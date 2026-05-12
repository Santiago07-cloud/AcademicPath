package com.academicpath.backend.service.impl;

import com.academicpath.backend.dto.request.UpdateUsuarioRequest;
import com.academicpath.backend.dto.response.UsuarioResponse;
import com.academicpath.backend.entity.Usuario;
import com.academicpath.backend.exception.ResourceNotFoundException;
import com.academicpath.backend.mapper.UsuarioMapper;
import com.academicpath.backend.repository.UsuarioRepository;
import com.academicpath.backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponse> obtenerTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(usuarioMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .map(usuarioMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse obtenerPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .map(usuarioMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con correo: " + correo));
    }

    @Override
    @Transactional
    public UsuarioResponse actualizar(Long id, UpdateUsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        if (request.getNombres() != null && !request.getNombres().isBlank()) {
            usuario.setNombres(request.getNombres());
        }
        if (request.getApellidos() != null && !request.getApellidos().isBlank()) {
            usuario.setApellidos(request.getApellidos());
        }
        if (request.getUniversidad() != null) {
            usuario.setUniversidad(request.getUniversidad());
        }
        if (request.getCarrera() != null) {
            usuario.setCarrera(request.getCarrera());
        }

        return usuarioMapper.toResponse(usuarioRepository.save(usuario));
    }
}
