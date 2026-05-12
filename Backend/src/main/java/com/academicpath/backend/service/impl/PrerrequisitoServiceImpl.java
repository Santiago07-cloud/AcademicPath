package com.academicpath.backend.service.impl;

import com.academicpath.backend.dto.request.PrerrequisitoRequest;
import com.academicpath.backend.dto.response.PrerrequisitoResponse;
import com.academicpath.backend.entity.Materia;
import com.academicpath.backend.entity.Prerrequisito;
import com.academicpath.backend.exception.MateriaException;
import com.academicpath.backend.exception.ResourceNotFoundException;
import com.academicpath.backend.repository.MateriaRepository;
import com.academicpath.backend.repository.PrerrequisitosRepository;
import com.academicpath.backend.repository.UsuarioMateriaRepository;
import com.academicpath.backend.service.PrerrequisitoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PrerrequisitoServiceImpl implements PrerrequisitoService {

    @Autowired
    private PrerrequisitosRepository prerrequisitosRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    @Autowired
    private UsuarioMateriaRepository usuarioMateriaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Long> obtenerPrerrequisitosMateria(Long materiaId) {
        return prerrequisitosRepository.findByMateriaId(materiaId)
                .stream()
                .map(p -> p.getMateriaPrerequisito().getId())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Long> calcularMateriasDisponibles(Long usuarioId) {
        // Evita N+1: carga todos los prerrequisitos de una vez y procesa en memoria
        Map<Long, List<Long>> prerrequisitosMap = prerrequisitosRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        p -> p.getMateria().getId(),
                        Collectors.mapping(p -> p.getMateriaPrerequisito().getId(), Collectors.toList())
                ));

        Set<Long> aprobadas = obtenerMateriasCursadasAprobadas(usuarioId);

        Set<Long> disponibles = new HashSet<>();
        List<Long> todasLasMaterias = materiaRepository.findAll()
                .stream()
                .map(Materia::getId)
                .toList();

        for (Long materiaId : todasLasMaterias) {
            List<Long> requisitos = prerrequisitosMap.getOrDefault(materiaId, List.of());
            if (aprobadas.containsAll(requisitos)) {
                disponibles.add(materiaId);
            }
        }
        return disponibles;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verificarPrerrequisitosCompletos(Long usuarioId, Long materiaId) {
        List<Long> prerequisitos = obtenerPrerrequisitosMateria(materiaId);
        if (prerequisitos.isEmpty()) return true;
        return obtenerMateriasCursadasAprobadas(usuarioId).containsAll(prerequisitos);
    }

    @Override
    @Transactional
    public PrerrequisitoResponse crearPrerrequisito(PrerrequisitoRequest request) {
        if (request.getMateriaId().equals(request.getMateriaPrerrequisitId())) {
            throw new MateriaException("Una materia no puede ser prerrequisito de sí misma");
        }

        Materia materia = materiaRepository.findById(request.getMateriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Materia no encontrada con id: " + request.getMateriaId()));

        Materia materiaPrerrequisit = materiaRepository.findById(request.getMateriaPrerrequisitId())
                .orElseThrow(() -> new ResourceNotFoundException("Materia prerrequisito no encontrada con id: " + request.getMateriaPrerrequisitId()));

        boolean yaExiste = prerrequisitosRepository.findByMateriaId(request.getMateriaId())
                .stream()
                .anyMatch(p -> p.getMateriaPrerequisito().getId().equals(request.getMateriaPrerrequisitId()));

        if (yaExiste) {
            throw new MateriaException("Este prerrequisito ya existe para la materia indicada");
        }

        Prerrequisito prerrequisito = Prerrequisito.builder()
                .materia(materia)
                .materiaPrerequisito(materiaPrerrequisit)
                .build();

        Prerrequisito guardado = prerrequisitosRepository.save(prerrequisito);

        return PrerrequisitoResponse.builder()
                .id(guardado.getId())
                .materiaId(materia.getId())
                .materiaNombre(materia.getNombre())
                .materiaPrerrequisitId(materiaPrerrequisit.getId())
                .materiaPrerrequisitNombre(materiaPrerrequisit.getNombre())
                .fechaCreacion(guardado.getFechaCreacion())
                .build();
    }

    @Override
    @Transactional
    public void eliminarPrerrequisito(Long prerrequisitId) {
        Prerrequisito prerrequisito = prerrequisitosRepository.findById(prerrequisitId)
                .orElseThrow(() -> new ResourceNotFoundException("Prerrequisito no encontrado con id: " + prerrequisitId));
        prerrequisitosRepository.delete(prerrequisito);
    }

    private Set<Long> obtenerMateriasCursadasAprobadas(Long usuarioId) {
        return usuarioMateriaRepository.findAprobadosByUsuarioId(usuarioId)
                .stream()
                .map(um -> um.getMateria().getId())
                .collect(Collectors.toSet());
    }
}
