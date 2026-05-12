package com.academicpath.backend.service.impl;

import com.academicpath.backend.dto.request.ProfesorRequest;
import com.academicpath.backend.dto.response.ProfesorResponse;
import com.academicpath.backend.entity.Profesor;
import com.academicpath.backend.exception.MateriaException;
import com.academicpath.backend.exception.ResourceNotFoundException;
import com.academicpath.backend.mapper.ProfesorMapper;
import com.academicpath.backend.repository.ProfesorRepository;
import com.academicpath.backend.service.ProfesorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProfesorServiceImpl implements ProfesorService {

    @Autowired
    private ProfesorRepository profesorRepository;

    @Autowired
    private ProfesorMapper profesorMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProfesorResponse> obtenerTodos() {
        return profesorRepository.findAll()
                .stream()
                .map(profesorMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProfesorResponse obtenerPorId(Long id) {
        return profesorRepository.findById(id)
                .map(profesorMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Profesor no encontrado con id: " + id));
    }

    @Override
    @Transactional
    public ProfesorResponse crear(ProfesorRequest request) {
        if (profesorRepository.existsByCorreo(request.getCorreo())) {
            throw new MateriaException("Ya existe un profesor con el correo: " + request.getCorreo());
        }

        Profesor profesor = Profesor.builder()
                .nombre(request.getNombre())
                .correo(request.getCorreo())
                .build();

        return profesorMapper.toResponse(profesorRepository.save(profesor));
    }

    @Override
    @Transactional
    public ProfesorResponse actualizar(Long id, ProfesorRequest request) {
        Profesor profesor = profesorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profesor no encontrado con id: " + id));

        if (!profesor.getCorreo().equals(request.getCorreo()) &&
                profesorRepository.existsByCorreo(request.getCorreo())) {
            throw new MateriaException("Ya existe un profesor con el correo: " + request.getCorreo());
        }

        profesor.setNombre(request.getNombre());
        profesor.setCorreo(request.getCorreo());

        return profesorMapper.toResponse(profesorRepository.save(profesor));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Profesor profesor = profesorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profesor no encontrado con id: " + id));
        profesorRepository.delete(profesor);
    }
}
