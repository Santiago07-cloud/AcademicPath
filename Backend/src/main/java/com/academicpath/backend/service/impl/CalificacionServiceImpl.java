package com.academicpath.backend.service.impl;

import com.academicpath.backend.dto.request.CalificacionRequest;
import com.academicpath.backend.dto.response.CalificacionResponse;
import com.academicpath.backend.exception.ResourceNotFoundException;
import com.academicpath.backend.mapper.CalificacionesMapper;
import com.academicpath.backend.models.entity.Actividades;
import com.academicpath.backend.models.entity.Calificaciones;
import com.academicpath.backend.repository.ActividadesRepository;
import com.academicpath.backend.repository.CalificacionesRepository;
import com.academicpath.backend.service.CalificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CalificacionServiceImpl implements CalificacionService {

    @Autowired
    private CalificacionesRepository calificacionesRepository;

    @Autowired
    private ActividadesRepository actividadesRepository;

    @Autowired
    private CalificacionesMapper calificacionesMapper;

    @Override
    @Transactional
    public CalificacionResponse crear(CalificacionRequest request) {
        Actividades actividad = actividadesRepository.findById(request.getActividadId())
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada con id: " + request.getActividadId()));

        if (request.getNota() < 0 || request.getNota() > actividad.getNotaMaxima()) {
            throw new IllegalArgumentException("La nota debe estar entre 0 y " + actividad.getNotaMaxima());
        }

        Calificaciones calificacion = Calificaciones.builder()
                .actividad(actividad)
                .nota(request.getNota())
                .retroalimentacion(request.getRetroalimentacion())
                .build();

        Calificaciones calificacionGuardada = calificacionesRepository.save(calificacion);
        return calificacionesMapper.toResponse(calificacionGuardada);
    }

    @Override
    public CalificacionResponse obtenerPorId(Long id) {
        Calificaciones calificacion = calificacionesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Calificación no encontrada con id: " + id));
        return calificacionesMapper.toResponse(calificacion);
    }

    @Override
    public List<CalificacionResponse> obtenerPorActividad(Long actividadId) {
        return calificacionesRepository.findByActividadId(actividadId)
                .stream()
                .map(calificacionesMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CalificacionResponse actualizar(Long id, CalificacionRequest request) {
        Calificaciones calificacion = calificacionesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Calificación no encontrada con id: " + id));

        Actividades actividad = calificacion.getActividad();

        if (request.getNota() < 0 || request.getNota() > actividad.getNotaMaxima()) {
            throw new IllegalArgumentException("La nota debe estar entre 0 y " + actividad.getNotaMaxima());
        }

        calificacion.setNota(request.getNota());
        calificacion.setRetroalimentacion(request.getRetroalimentacion());

        Calificaciones calificacionActualizada = calificacionesRepository.save(calificacion);
        return calificacionesMapper.toResponse(calificacionActualizada);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Calificaciones calificacion = calificacionesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Calificación no encontrada con id: " + id));
        calificacionesRepository.delete(calificacion);
    }
}

