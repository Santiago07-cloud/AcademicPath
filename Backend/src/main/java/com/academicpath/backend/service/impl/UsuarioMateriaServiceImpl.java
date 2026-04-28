package com.academicpath.backend.service.impl;

import com.academicpath.backend.dto.request.UsuarioMateriaRequest;
import com.academicpath.backend.dto.response.UsuarioMateriaResponse;
import com.academicpath.backend.exception.ResourceNotFoundException;
import com.academicpath.backend.exception.UsuarioException;
import com.academicpath.backend.models.entity.Materias;
import com.academicpath.backend.models.entity.Profesores;
import com.academicpath.backend.models.entity.Usuarios;
import com.academicpath.backend.models.entity.UsuarioMaterias;
import com.academicpath.backend.repository.MateriasRepository;
import com.academicpath.backend.repository.ProfesoresRepository;
import com.academicpath.backend.repository.UsuariosRepository;
import com.academicpath.backend.repository.UsuarioMateriasRepository;
import com.academicpath.backend.service.PrerrequisitoService;
import com.academicpath.backend.service.UsuarioMateriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioMateriaServiceImpl implements UsuarioMateriaService {

    @Autowired
    private UsuarioMateriasRepository usuarioMateriasRepository;

    @Autowired
    private UsuariosRepository usuariosRepository;

    @Autowired
    private MateriasRepository materiasRepository;

    @Autowired
    private ProfesoresRepository profesoresRepository;

    @Autowired
    private PrerrequisitoService prerrequisitosService;

    @Override
    @Transactional
    public UsuarioMateriaResponse inscribir(UsuarioMateriaRequest request) {
        Usuarios usuario = usuariosRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + request.getUsuarioId()));

        Materias materia = materiasRepository.findById(request.getMateriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Materia no encontrada con id: " + request.getMateriaId()));

        // Profesor es opcional
        Profesores profesor = null;
        if (request.getProfesorId() != null) {
            profesor = profesoresRepository.findById(request.getProfesorId())
                    .orElse(null);
        }

        // Verificar si ya está inscrito
        if (usuarioMateriasRepository.findByUsuarioIdAndMateriaId(request.getUsuarioId(), request.getMateriaId()).isPresent()) {
            throw new UsuarioException("El usuario ya está inscrito en esta materia");
        }

        // Verificar prerrequisitos (solo si existen prerrequisitos definidos)
        if (!prerrequisitosService.verificarPrerrequisitosCompletos(request.getUsuarioId(), request.getMateriaId())) {
            throw new UsuarioException("El usuario no cumple con los prerrequisitos de esta materia");
        }

        String estado = (request.getEstado() != null && !request.getEstado().isBlank())
                ? request.getEstado()
                : "CURSANDO";

        UsuarioMaterias usuarioMateria = UsuarioMaterias.builder()
                .usuario(usuario)
                .materia(materia)
                .profesor(profesor)
                .semestre(request.getSemestre())
                .anio(request.getAnio())
                .estado(estado)
                .notaFinal(request.getNotaFinal() != null ? request.getNotaFinal() : 0.0)
                .build();

        UsuarioMaterias guardada = usuarioMateriasRepository.save(usuarioMateria);
        return mapToResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioMateriaResponse obtenerPorId(Long id) {
        UsuarioMaterias usuarioMateria = usuarioMateriasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario-Materia no encontrada con id: " + id));
        return mapToResponse(usuarioMateria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioMateriaResponse> obtenerPorUsuario(Long usuarioId) {
        return usuarioMateriasRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UsuarioMateriaResponse actualizar(Long id, UsuarioMateriaRequest request) {
        UsuarioMaterias usuarioMateria = usuarioMateriasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario-Materia no encontrada con id: " + id));

        // Profesor opcional en actualización
        if (request.getProfesorId() != null) {
            Profesores profesor = profesoresRepository.findById(request.getProfesorId()).orElse(null);
            usuarioMateria.setProfesor(profesor);
        }

        if (request.getSemestre() != null) {
            usuarioMateria.setSemestre(request.getSemestre());
        }
        if (request.getAnio() != null) {
            usuarioMateria.setAnio(request.getAnio());
        }
        if (request.getEstado() != null) {
            usuarioMateria.setEstado(request.getEstado());
        }
        if (request.getNotaFinal() != null) {
            usuarioMateria.setNotaFinal(request.getNotaFinal());
        }

        UsuarioMaterias actualizada = usuarioMateriasRepository.save(usuarioMateria);
        return mapToResponse(actualizada);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        UsuarioMaterias usuarioMateria = usuarioMateriasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario-Materia no encontrada con id: " + id));
        usuarioMateriasRepository.delete(usuarioMateria);
    }

    private UsuarioMateriaResponse mapToResponse(UsuarioMaterias um) {
        UsuarioMateriaResponse.MateriaInfo materiaInfo = UsuarioMateriaResponse.MateriaInfo.builder()
                .id(um.getMateria().getId())
                .codigo(um.getMateria().getCodigo())
                .nombre(um.getMateria().getNombre())
                .creditos(um.getMateria().getCreditos())
                .descripcion(um.getMateria().getDescripcion())
                .build();

        return UsuarioMateriaResponse.builder()
                .id(um.getId())
                .usuarioId(um.getUsuario().getId())
                .materiaId(um.getMateria().getId())
                .materia(materiaInfo)
                .materiaNombre(um.getMateria().getNombre())
                .profesorId(um.getProfesor() != null ? um.getProfesor().getId() : null)
                .profesorNombre(um.getProfesor() != null ? um.getProfesor().getNombre() : null)
                .semestre(um.getSemestre())
                .anio(um.getAnio())
                .estado(um.getEstado())
                .notaFinal(um.getNotaFinal())
                .fechaCreacion(um.getFechaCreacion())
                .build();
    }
}
