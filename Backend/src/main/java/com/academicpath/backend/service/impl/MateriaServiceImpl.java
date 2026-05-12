package com.academicpath.backend.service.impl;

import com.academicpath.backend.dto.request.MateriaRequest;
import com.academicpath.backend.dto.response.MateriaResponse;
import com.academicpath.backend.entity.Materia;
import com.academicpath.backend.exception.MateriaException;
import com.academicpath.backend.exception.ResourceNotFoundException;
import com.academicpath.backend.mapper.MateriaMapper;
import com.academicpath.backend.repository.MateriaRepository;
import com.academicpath.backend.service.MateriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MateriaServiceImpl implements MateriaService {

    @Autowired
    private MateriaRepository materiaRepository;

    @Autowired
    private MateriaMapper materiaMapper;

    @Override
    @Transactional
    public MateriaResponse crear(MateriaRequest request) {
        if (materiaRepository.existsByCodigo(request.getCodigo())) {
            throw new MateriaException("El código de materia ya existe: " + request.getCodigo());
        }

        Materia materia = Materia.builder()
                .codigo(request.getCodigo())
                .nombre(request.getNombre())
                .creditos(request.getCreditos())
                .descripcion(request.getDescripcion())
                .build();

        return materiaMapper.toResponse(materiaRepository.save(materia));
    }

    @Override
    @Transactional(readOnly = true)
    public MateriaResponse obtenerPorId(Long id) {
        return materiaRepository.findById(id)
                .map(materiaMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Materia no encontrada con id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MateriaResponse> obtenerTodas() {
        return materiaRepository.findAll()
                .stream()
                .map(materiaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public MateriaResponse actualizar(Long id, MateriaRequest request) {
        Materia materia = materiaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Materia no encontrada con id: " + id));

        if (!materia.getCodigo().equals(request.getCodigo()) && materiaRepository.existsByCodigo(request.getCodigo())) {
            throw new MateriaException("El código de materia ya existe: " + request.getCodigo());
        }

        materia.setCodigo(request.getCodigo());
        materia.setNombre(request.getNombre());
        materia.setCreditos(request.getCreditos());
        materia.setDescripcion(request.getDescripcion());

        return materiaMapper.toResponse(materiaRepository.save(materia));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Materia materia = materiaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Materia no encontrada con id: " + id));
        materiaRepository.delete(materia);
    }
}
