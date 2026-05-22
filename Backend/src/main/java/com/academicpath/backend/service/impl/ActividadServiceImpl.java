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

    @Autowired private ActividadRepository actividadRepository;
    @Autowired private UsuarioMateriaRepository usuarioMateriaRepository;
    @Autowired private ActividadMapper actividadMapper;
    @Autowired private MateriaProgressService progressService;

    @Override
    @Transactional
    public ActividadResponse crear(ActividadRequest request) {
        UsuarioMateria usuarioMateria = usuarioMateriaRepository.findById(request.getUsuarioMateriaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Usuario-Materia no encontrada con id: " + request.getUsuarioMateriaId()));

        // VALIDAR: no se puede agregar actividades a una materia cerrada
        progressService.validarMateriaAbierta(usuarioMateria, "crear actividad");

        validarPeso(request.getPeso(), request.getUsuarioMateriaId(), null);

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

        // VALIDAR: no se puede editar actividades de una materia cerrada
        progressService.validarMateriaAbierta(actividad.getUsuarioMateria(), "editar actividad");

        validarPeso(request.getPeso(), actividad.getUsuarioMateria().getId(), id);

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

        // VALIDAR: no se puede eliminar actividades de una materia cerrada
        progressService.validarMateriaAbierta(actividad.getUsuarioMateria(), "eliminar actividad");

        actividadRepository.delete(actividad);
    }

    /**
     * Valida que el peso no supere 100% considerando las actividades existentes.
     * Si actividadIdExcluir es != null, excluye esa actividad del calculo (caso edicion).
     */
    private void validarPeso(Double peso, Long usuarioMateriaId, Long actividadIdExcluir) {
        if (peso <= 0 || peso > 100) {
            throw new ActividadException("El peso debe estar entre 1 y 100.");
        }

        List<Actividad> existentes = actividadRepository.findByUsuarioMateriaId(usuarioMateriaId);
        double pesoActual = existentes.stream()
                .filter(a -> !a.getId().equals(actividadIdExcluir))
                .mapToDouble(Actividad::getPeso)
                .sum();

        if (pesoActual + peso > 100.0) {
            throw new ActividadException(
                String.format(
                    "El peso total superaria 100%%. Peso acumulado actual: %.1f%%. " +
                    "Maximo disponible para esta actividad: %.1f%%.",
                    pesoActual, 100.0 - pesoActual
                )
            );
        }
    }
}
