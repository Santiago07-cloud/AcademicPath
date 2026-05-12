package com.academicpath.backend.service.impl;

import com.academicpath.backend.dto.request.ActividadRequest;
import com.academicpath.backend.dto.response.ActividadResponse;
import com.academicpath.backend.entity.Actividad;
import com.academicpath.backend.entity.UsuarioMateria;
import com.academicpath.backend.exception.ActividadException;
import com.academicpath.backend.exception.ResourceNotFoundException;
import com.academicpath.backend.mapper.ActividadMapper;
import com.academicpath.backend.repository.ActividadRepository;
import com.academicpath.backend.repository.UsuarioMateriaRepository;
import com.academicpath.backend.service.ActividadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ActividadServiceImpl implements ActividadService {

    @Autowired
    private ActividadRepository actividadRepository;

    @Autowired
    private UsuarioMateriaRepository usuarioMateriaRepository;

    @Autowired
    private ActividadMapper actividadMapper;

    @Override
    @Transactional
    public ActividadResponse crear(ActividadRequest request) {
        UsuarioMateria usuarioMateria = usuarioMateriaRepository.findById(request.getUsuarioMateriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario-Materia no encontrada con id: " + request.getUsuarioMateriaId()));

        validarPeso(request.getPeso());

        Actividad actividad = Actividad.builder()
                .usuarioMateria(usuarioMateria)
                .titulo(request.getTitulo())
                .tipo(request.getTipo())
                .peso(request.getPeso())
                .notaMaxima(request.getNotaMaxima())
                .fechaEntrega(request.getFechaEntrega())
                .build();

        return actividadMapper.toResponse(actividadRepository.save(actividad));
    }

    @Override
    @Transactional(readOnly = true)
    public ActividadResponse obtenerPorId(Long id) {
        return actividadRepository.findById(id)
                .map(actividadMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada con id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActividadResponse> obtenerPorUsuarioMateria(Long usuarioMateriaId) {
        return actividadRepository.findByUsuarioMateriaId(usuarioMateriaId)
                .stream()
                .map(actividadMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ActividadResponse actualizar(Long id, ActividadRequest request) {
        Actividad actividad = actividadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada con id: " + id));

        validarPeso(request.getPeso());

        actividad.setTitulo(request.getTitulo());
        actividad.setTipo(request.getTipo());
        actividad.setPeso(request.getPeso());
        actividad.setNotaMaxima(request.getNotaMaxima());
        actividad.setFechaEntrega(request.getFechaEntrega());

        return actividadMapper.toResponse(actividadRepository.save(actividad));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Actividad actividad = actividadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada con id: " + id));
        actividadRepository.delete(actividad);
    }

    private void validarPeso(Double peso) {
        if (peso < 0 || peso > 100) {
            throw new ActividadException("El peso de la actividad debe estar entre 0 y 100");
        }
    }
}
