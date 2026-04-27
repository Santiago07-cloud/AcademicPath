package com.academicpath.backend.service.impl;

import com.academicpath.backend.dto.request.MateriaRequest;
import com.academicpath.backend.dto.response.MateriaResponse;
import com.academicpath.backend.exception.MateriaException;
import com.academicpath.backend.exception.ResourceNotFoundException;
import com.academicpath.backend.mapper.MateriasMapper;
import com.academicpath.backend.models.entity.Materias;
import com.academicpath.backend.repository.MateriasRepository;
import com.academicpath.backend.service.MateriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MateriaServiceImpl implements MateriaService {

    @Autowired
    private MateriasRepository materiasRepository;

    @Autowired
    private MateriasMapper materiasMapper;

    @Override
    @Transactional
    public MateriaResponse crear(MateriaRequest request) {
        if (materiasRepository.existsByCodigo(request.getCodigo())) {
            throw new MateriaException("El código de materia ya existe: " + request.getCodigo());
        }

        Materias materia = Materias.builder()
                .codigo(request.getCodigo())
                .nombre(request.getNombre())
                .creditos(request.getCreditos())
                .descripcion(request.getDescripcion())
                .build();

        Materias materiaGuardada = materiasRepository.save(materia);
        return materiasMapper.toResponse(materiaGuardada);
    }

    @Override
    public MateriaResponse obtenerPorId(Long id) {
        Materias materia = materiasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Materia no encontrada con id: " + id));
        return materiasMapper.toResponse(materia);
    }

    @Override
    public List<MateriaResponse> obtenerTodas() {
        return materiasRepository.findAll()
                .stream()
                .map(materiasMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MateriaResponse actualizar(Long id, MateriaRequest request) {
        Materias materia = materiasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Materia no encontrada con id: " + id));

        if (!materia.getCodigo().equals(request.getCodigo()) && 
            materiasRepository.existsByCodigo(request.getCodigo())) {
            throw new MateriaException("El código de materia ya existe: " + request.getCodigo());
        }

        materia.setCodigo(request.getCodigo());
        materia.setNombre(request.getNombre());
        materia.setCreditos(request.getCreditos());
        materia.setDescripcion(request.getDescripcion());

        Materias materiaActualizada = materiasRepository.save(materia);
        return materiasMapper.toResponse(materiaActualizada);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Materias materia = materiasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Materia no encontrada con id: " + id));
        materiasRepository.delete(materia);
    }
}

