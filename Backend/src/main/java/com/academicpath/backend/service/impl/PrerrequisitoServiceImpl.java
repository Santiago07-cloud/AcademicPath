package com.academicpath.backend.service.impl;

import com.academicpath.backend.exception.ResourceNotFoundException;
import com.academicpath.backend.repository.MateriaRepository;
import com.academicpath.backend.repository.PrerrequisitosRepository;
import com.academicpath.backend.repository.UsuarioMateriaRepository;
import com.academicpath.backend.service.PrerrequisitoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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
    public List<Long> obtenerPrerrequisitosMateria(Long materiaId) {
        return prerrequisitosRepository.findByMateriaId(materiaId)
                .stream()
                .map(p -> p.getMateriaPrerequisito().getId())
                .toList();
    }

    @Override
    public Set<Long> calcularMateriasDisponibles(Long usuarioId) {
        Set<Long> materiasDisponibles = new HashSet<>();
        List<Long> todasLasMaterias = materiaRepository.findAll()
                .stream()
                .map(m -> m.getId())
                .toList();

        for (Long materiaId : todasLasMaterias) {
            if (verificarPrerrequisitosCompletos(usuarioId, materiaId)) {
                materiasDisponibles.add(materiaId);
            }
        }
        return materiasDisponibles;
    }

    @Override
    public boolean verificarPrerrequisitosCompletos(Long usuarioId, Long materiaId) {
        List<Long> prerequisitos = obtenerPrerrequisitosMateria(materiaId);
        if (prerequisitos.isEmpty()) {
            return true;
        }
        Set<Long> materiasCursadasAprobadas = obtenerMateriasCursadasAprobadas(usuarioId);
        return materiasCursadasAprobadas.containsAll(prerequisitos);
    }

    private Set<Long> obtenerMateriasCursadasAprobadas(Long usuarioId) {
        return usuarioMateriaRepository.findAprobadosByUsuarioId(usuarioId)
                .stream()
                .map(um -> um.getMateria().getId())
                .collect(Collectors.toSet());
    }
}
