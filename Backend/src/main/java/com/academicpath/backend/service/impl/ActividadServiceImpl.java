package com.academicpath.backend.service.impl;

import com.academicpath.backend.dto.request.ActividadRequest;
import com.academicpath.backend.dto.response.ActividadResponse;
import com.academicpath.backend.exception.ActividadException;
import com.academicpath.backend.exception.ResourceNotFoundException;
import com.academicpath.backend.mapper.ActividadesMapper;
import com.academicpath.backend.models.entity.Actividades;
import com.academicpath.backend.models.entity.UsuarioMaterias;
import com.academicpath.backend.repository.ActividadesRepository;
import com.academicpath.backend.repository.UsuarioMateriasRepository;
import com.academicpath.backend.service.ActividadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActividadServiceImpl implements ActividadService {

    @Autowired
    private ActividadesRepository actividadesRepository;

    @Autowired
    private UsuarioMateriasRepository usuarioMateriasRepository;

    @Autowired
    private ActividadesMapper actividadesMapper;

    @Override
    @Transactional
    public ActividadResponse crear(ActividadRequest request) {
        UsuarioMaterias usuarioMateria = usuarioMateriasRepository.findById(request.getUsuarioMateriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario-Materia no encontrada con id: " + request.getUsuarioMateriaId()));

        if (request.getPeso() < 0 || request.getPeso() > 100) {
            throw new ActividadException("El peso de la actividad debe estar entre 0 y 100");
        }

        Actividades actividad = Actividades.builder()
                .usuarioMateria(usuarioMateria)
                .titulo(request.getTitulo())
                .tipo(request.getTipo())
                .peso(request.getPeso())
                .notaMaxima(request.getNotaMaxima())
                .fechaEntrega(request.getFechaEntrega())
                .build();

        Actividades actividadGuardada = actividadesRepository.save(actividad);
        return actividadesMapper.toResponse(actividadGuardada);
    }

    @Override
    public ActividadResponse obtenerPorId(Long id) {
        Actividades actividad = actividadesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada con id: " + id));
        return actividadesMapper.toResponse(actividad);
    }

    @Override
    public List<ActividadResponse> obtenerPorUsuarioMateria(Long usuarioMateriaId) {
        return actividadesRepository.findByUsuarioMateriaId(usuarioMateriaId)
                .stream()
                .map(actividadesMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ActividadResponse actualizar(Long id, ActividadRequest request) {
        Actividades actividad = actividadesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada con id: " + id));

        if (request.getPeso() < 0 || request.getPeso() > 100) {
            throw new ActividadException("El peso de la actividad debe estar entre 0 y 100");
        }

        actividad.setTitulo(request.getTitulo());
        actividad.setTipo(request.getTipo());
        actividad.setPeso(request.getPeso());
        actividad.setNotaMaxima(request.getNotaMaxima());
        actividad.setFechaEntrega(request.getFechaEntrega());

        Actividades actividadActualizada = actividadesRepository.save(actividad);
        return actividadesMapper.toResponse(actividadActualizada);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Actividades actividad = actividadesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada con id: " + id));
        actividadesRepository.delete(actividad);
    }
}

