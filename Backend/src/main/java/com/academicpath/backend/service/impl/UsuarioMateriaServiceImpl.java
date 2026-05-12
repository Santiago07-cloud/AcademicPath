package com.academicpath.backend.service.impl;

import com.academicpath.backend.dto.request.UsuarioMateriaRequest;
import com.academicpath.backend.dto.response.UsuarioMateriaResponse;
import com.academicpath.backend.entity.Materia;
import com.academicpath.backend.entity.Profesor;
import com.academicpath.backend.entity.Usuario;
import com.academicpath.backend.entity.UsuarioMateria;
import com.academicpath.backend.exception.ResourceNotFoundException;
import com.academicpath.backend.exception.UsuarioException;
import com.academicpath.backend.mapper.UsuarioMateriaMapper;
import com.academicpath.backend.repository.MateriaRepository;
import com.academicpath.backend.repository.ProfesorRepository;
import com.academicpath.backend.repository.UsuarioMateriaRepository;
import com.academicpath.backend.repository.UsuarioRepository;
import com.academicpath.backend.service.PrerrequisitoService;
import com.academicpath.backend.service.UsuarioMateriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioMateriaServiceImpl implements UsuarioMateriaService {

    @Autowired
    private UsuarioMateriaRepository usuarioMateriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    @Autowired
    private ProfesorRepository profesorRepository;

    @Autowired
    private PrerrequisitoService prerrequisitosService;

    @Autowired
    private UsuarioMateriaMapper usuarioMateriaMapper;

    @Override
    @Transactional
    public UsuarioMateriaResponse inscribir(UsuarioMateriaRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + request.getUsuarioId()));

        Materia materia = materiaRepository.findById(request.getMateriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Materia no encontrada con id: " + request.getMateriaId()));

        if (usuarioMateriaRepository.findByUsuarioIdAndMateriaId(request.getUsuarioId(), request.getMateriaId()).isPresent()) {
            throw new UsuarioException("El usuario ya está inscrito en esta materia");
        }

        if (!prerrequisitosService.verificarPrerrequisitosCompletos(request.getUsuarioId(), request.getMateriaId())) {
            throw new UsuarioException("El usuario no cumple con los prerrequisitos de esta materia");
        }

        Profesor profesor = null;
        if (request.getProfesorId() != null) {
            profesor = profesorRepository.findById(request.getProfesorId()).orElse(null);
        }

        String estado = (request.getEstado() != null && !request.getEstado().isBlank())
                ? request.getEstado() : "CURSANDO";

        UsuarioMateria usuarioMateria = UsuarioMateria.builder()
                .usuario(usuario)
                .materia(materia)
                .profesor(profesor)
                .semestre(request.getSemestre())
                .anio(request.getAnio())
                .estado(estado)
                .notaFinal(request.getNotaFinal() != null ? request.getNotaFinal() : 0.0)
                .build();

        return usuarioMateriaMapper.toResponse(usuarioMateriaRepository.save(usuarioMateria));
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioMateriaResponse obtenerPorId(Long id) {
        return usuarioMateriaRepository.findById(id)
                .map(usuarioMateriaMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario-Materia no encontrada con id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioMateriaResponse> obtenerPorUsuario(Long usuarioId) {
        return usuarioMateriaRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(usuarioMateriaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public UsuarioMateriaResponse actualizar(Long id, UsuarioMateriaRequest request) {
        UsuarioMateria usuarioMateria = usuarioMateriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario-Materia no encontrada con id: " + id));

        if (request.getProfesorId() != null) {
            usuarioMateria.setProfesor(profesorRepository.findById(request.getProfesorId()).orElse(null));
        }
        if (request.getSemestre() != null) usuarioMateria.setSemestre(request.getSemestre());
        if (request.getAnio() != null) usuarioMateria.setAnio(request.getAnio());
        if (request.getEstado() != null) usuarioMateria.setEstado(request.getEstado());
        if (request.getNotaFinal() != null) usuarioMateria.setNotaFinal(request.getNotaFinal());

        return usuarioMateriaMapper.toResponse(usuarioMateriaRepository.save(usuarioMateria));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        UsuarioMateria usuarioMateria = usuarioMateriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario-Materia no encontrada con id: " + id));
        usuarioMateriaRepository.delete(usuarioMateria);
    }
}
