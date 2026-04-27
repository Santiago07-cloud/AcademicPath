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

        Profesores profesor = profesoresRepository.findById(request.getProfesorId())
                .orElseThrow(() -> new ResourceNotFoundException("Profesor no encontrado con id: " + request.getProfesorId()));

        if (usuarioMateriasRepository.findByUsuarioIdAndMateriaId(request.getUsuarioId(), request.getMateriaId()).isPresent()) {
            throw new UsuarioException("El usuario ya está inscrito en esta materia");
        }

        if (!prerrequisitosService.verificarPrerrequisitosCompletos(request.getUsuarioId(), request.getMateriaId())) {
            throw new UsuarioException("El usuario no cumple con los prerrequisitos de esta materia");
        }

        UsuarioMaterias usuarioMateria = UsuarioMaterias.builder()
                .usuario(usuario)
                .materia(materia)
                .profesor(profesor)
                .semestre(request.getSemestre())
                .anio(request.getAnio())
                .estado("CURSANDO")
                .notaFinal(0.0)
                .build();

        UsuarioMaterias usuarioMateriaGuardada = usuarioMateriasRepository.save(usuarioMateria);
        return mapToResponse(usuarioMateriaGuardada);
    }

    @Override
    public UsuarioMateriaResponse obtenerPorId(Long id) {
        UsuarioMaterias usuarioMateria = usuarioMateriasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario-Materia no encontrada con id: " + id));
        return mapToResponse(usuarioMateria);
    }

    @Override
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

        Profesores profesor = profesoresRepository.findById(request.getProfesorId())
                .orElseThrow(() -> new ResourceNotFoundException("Profesor no encontrado con id: " + request.getProfesorId()));

        usuarioMateria.setProfesor(profesor);
        usuarioMateria.setSemestre(request.getSemestre());
        usuarioMateria.setAnio(request.getAnio());

        UsuarioMaterias usuarioMateriaActualizada = usuarioMateriasRepository.save(usuarioMateria);
        return mapToResponse(usuarioMateriaActualizada);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        UsuarioMaterias usuarioMateria = usuarioMateriasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario-Materia no encontrada con id: " + id));
        usuarioMateriasRepository.delete(usuarioMateria);
    }

    private UsuarioMateriaResponse mapToResponse(UsuarioMaterias usuarioMateria) {
        return UsuarioMateriaResponse.builder()
                .id(usuarioMateria.getId())
                .usuarioId(usuarioMateria.getUsuario().getId())
                .materiaId(usuarioMateria.getMateria().getId())
                .materiaNombre(usuarioMateria.getMateria().getNombre())
                .profesorId(usuarioMateria.getProfesor().getId())
                .profesorNombre(usuarioMateria.getProfesor().getNombre())
                .semestre(usuarioMateria.getSemestre())
                .anio(usuarioMateria.getAnio())
                .estado(usuarioMateria.getEstado())
                .notaFinal(usuarioMateria.getNotaFinal())
                .fechaCreacion(usuarioMateria.getFechaCreacion())
                .build();
    }
}

