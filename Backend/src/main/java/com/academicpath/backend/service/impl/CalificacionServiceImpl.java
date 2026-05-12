package com.academicpath.backend.service.impl;

import com.academicpath.backend.dto.request.CalificacionRequest;
import com.academicpath.backend.dto.response.CalificacionResponse;
import com.academicpath.backend.entity.Actividad;
import com.academicpath.backend.entity.Calificacion;
import com.academicpath.backend.exception.CalificacionException;
import com.academicpath.backend.exception.ResourceNotFoundException;
import com.academicpath.backend.mapper.CalificacionMapper;
import com.academicpath.backend.repository.ActividadRepository;
import com.academicpath.backend.repository.CalificacionRepository;
import com.academicpath.backend.service.CalificacionService;
import com.academicpath.backend.service.ProgresoAcademicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CalificacionServiceImpl implements CalificacionService {

    @Autowired
    private CalificacionRepository calificacionRepository;

    @Autowired
    private ActividadRepository actividadRepository;

    @Autowired
    private CalificacionMapper calificacionMapper;

    @Autowired
    private ProgresoAcademicoService progresoAcademicoService;

    @Override
    @Transactional
    public CalificacionResponse crear(CalificacionRequest request) {
        Actividad actividad = actividadRepository.findById(request.getActividadId())
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada con id: " + request.getActividadId()));

        validarNota(request.getNota(), actividad.getNotaMaxima());

        Calificacion calificacion = Calificacion.builder()
                .actividad(actividad)
                .nota(request.getNota())
                .retroalimentacion(request.getRetroalimentacion())
                .build();

        CalificacionResponse response = calificacionMapper.toResponse(calificacionRepository.save(calificacion));

        // Recalcular progreso académico automáticamente
        Long usuarioId = actividad.getUsuarioMateria().getUsuario().getId();
        progresoAcademicoService.recalcularProgreso(usuarioId);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public CalificacionResponse obtenerPorId(Long id) {
        return calificacionRepository.findById(id)
                .map(calificacionMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Calificación no encontrada con id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CalificacionResponse> obtenerPorActividad(Long actividadId) {
        return calificacionRepository.findByActividadId(actividadId)
                .stream()
                .map(calificacionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CalificacionResponse actualizar(Long id, CalificacionRequest request) {
        Calificacion calificacion = calificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Calificación no encontrada con id: " + id));

        validarNota(request.getNota(), calificacion.getActividad().getNotaMaxima());

        calificacion.setNota(request.getNota());
        calificacion.setRetroalimentacion(request.getRetroalimentacion());

        CalificacionResponse response = calificacionMapper.toResponse(calificacionRepository.save(calificacion));

        // Recalcular progreso académico automáticamente
        Long usuarioId = calificacion.getActividad().getUsuarioMateria().getUsuario().getId();
        progresoAcademicoService.recalcularProgreso(usuarioId);

        return response;
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Calificacion calificacion = calificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Calificación no encontrada con id: " + id));
        calificacionRepository.delete(calificacion);
    }

    private void validarNota(Double nota, Double notaMaxima) {
        if (nota < 0 || nota > notaMaxima) {
            throw new CalificacionException("La nota debe estar entre 0 y " + notaMaxima);
        }
    }
}
