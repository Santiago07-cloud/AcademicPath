package com.academicpath.backend.service.impl;

import com.academicpath.backend.exception.ResourceNotFoundException;
import com.academicpath.backend.repository.MateriasRepository;
import com.academicpath.backend.repository.PrerrequsitosRepository;
import com.academicpath.backend.repository.UsuarioMateriasRepository;
import com.academicpath.backend.service.PrerrequisitoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PrerrequisitoServiceImpl implements PrerrequisitoService {

    @Autowired
    private PrerrequsitosRepository prerrequisitosRepository;

    @Autowired
    private MateriasRepository materiasRepository;

    @Autowired
    private UsuarioMateriasRepository usuarioMateriasRepository;

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
        Set<Long> materiasCursadas = obtenerMateriasCursadasAprobadas(usuarioId);

        List<Long> todasLasMaterias = materiasRepository.findAll()
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
        return usuarioMateriasRepository.findAprobadosByUsuarioId(usuarioId)
                .stream()
                .map(um -> um.getMateria().getId())
                .collect(java.util.stream.Collectors.toSet());
    }
}

