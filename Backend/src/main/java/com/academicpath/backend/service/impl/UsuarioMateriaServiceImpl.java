package com.academicpath.backend.service.impl;

import com.academicpath.backend.dto.request.UsuarioMateriaRequest;
import com.academicpath.backend.dto.response.UsuarioMateriaResponse;
import com.academicpath.backend.entity.Materia;
import com.academicpath.backend.entity.Usuario;
import com.academicpath.backend.entity.UsuarioMateria;
import com.academicpath.backend.exception.ResourceNotFoundException;
import com.academicpath.backend.exception.UsuarioException;
import com.academicpath.backend.mapper.UsuarioMateriaMapper;
import com.academicpath.backend.repository.MateriaRepository;
import com.academicpath.backend.repository.UsuarioMateriaRepository;
import com.academicpath.backend.repository.UsuarioRepository;
import com.academicpath.backend.service.PrerrequisitoService;
import com.academicpath.backend.service.ProgresoAcademicoService;
import com.academicpath.backend.service.UsuarioMateriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class UsuarioMateriaServiceImpl implements UsuarioMateriaService {

    private static final ZoneId ZONA_COLOMBIA = ZoneId.of("America/Bogota");

    private int anioActual() {
        return ZonedDateTime.now(ZONA_COLOMBIA).getYear();
    }

    private void validarAnioActivo(Integer anio) {
        if (anio == null) return;
        int actual = anioActual();
        if (anio < actual) {
            throw new UsuarioException(
                "No se pueden crear ni modificar registros de a\u00f1os anteriores. " +
                "A\u00f1o solicitado: " + anio + ". A\u00f1o activo: " + actual + "."
            );
        }
        if (anio > actual + 1) {
            throw new UsuarioException(
                "El a\u00f1o " + anio + " est\u00e1 demasiado en el futuro. " +
                "Solo se permite el a\u00f1o actual (" + actual + ") o el siguiente."
            );
        }
    }

    @Autowired
    private UsuarioMateriaRepository usuarioMateriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    @Autowired
    private PrerrequisitoService prerrequisitosService;

    @Autowired
    private ProgresoAcademicoService progresoAcademicoService;

    @Autowired
    private UsuarioMateriaMapper usuarioMateriaMapper;

    @Autowired
    private MateriaProgressService progressService;

    @Override
    @Transactional
    public UsuarioMateriaResponse inscribir(UsuarioMateriaRequest request) {
        // Validar que el año sea el activo
        validarAnioActivo(request.getAnio());

        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + request.getUsuarioId()));

        Materia materia = materiaRepository.findById(request.getMateriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Materia no encontrada con id: " + request.getMateriaId()));

        // Validar que no esté ya inscrito en esta materia en el mismo año/semestre
        boolean yaInscrito = usuarioMateriaRepository
                .findByUsuarioIdAndMateriaId(request.getUsuarioId(), request.getMateriaId())
                .stream()
                .anyMatch(um -> um.getAnio().equals(request.getAnio())
                        && um.getSemestre().equals(request.getSemestre())
                        && !um.getEstado().equals("APROBADA")
                        && !um.getEstado().equals("REPROBADA"));

        if (yaInscrito) {
            throw new UsuarioException("El usuario ya está inscrito en esta materia en el mismo semestre");
        }

        String estado = (request.getEstado() != null && !request.getEstado().isBlank())
                ? request.getEstado() : "CURSANDO";

        UsuarioMateria usuarioMateria = UsuarioMateria.builder()
                .usuario(usuario)
                .materia(materia)
                .semestre(request.getSemestre())
                .anio(request.getAnio())
                .estado(estado)
                .notaFinal(null) // null hasta que haya calificaciones reales
                .build();

        return usuarioMateriaMapper.toResponse(usuarioMateriaRepository.save(usuarioMateria));
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioMateriaResponse obtenerPorId(Long id) {
        UsuarioMateria um = usuarioMateriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario-Materia no encontrada con id: " + id));
        return enrichWithProgress(usuarioMateriaMapper.toResponse(um), um.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioMateriaResponse> obtenerPorUsuario(Long usuarioId) {
        return usuarioMateriaRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(um -> enrichWithProgress(usuarioMateriaMapper.toResponse(um), um.getId()))
                .toList();
    }

    @Override
    @Transactional
    public UsuarioMateriaResponse actualizar(Long id, UsuarioMateriaRequest request) {
        UsuarioMateria usuarioMateria = usuarioMateriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario-Materia no encontrada con id: " + id));

        if (request.getAnio() != null && !request.getAnio().equals(usuarioMateria.getAnio())) {
            validarAnioActivo(request.getAnio());
        }

        String estadoActual = usuarioMateria.getEstado();
        boolean estabaAbierta = !estadoActual.equals("APROBADA") && !estadoActual.equals("REPROBADA");
        boolean seEstaCerrando = request.getEstado() != null &&
                (request.getEstado().equals("APROBADA") || request.getEstado().equals("REPROBADA"));

        if (!estabaAbierta && !seEstaCerrando && request.getNotaFinal() == null) {
            throw new UsuarioException("No se puede modificar una materia ya finalizada");
        }

        // Si se intenta cerrar la materia, validar que el avance sea 100%
        if (seEstaCerrando && estabaAbierta) {
            progressService.validarCierreMateria(id, usuarioMateria);
        }

        if (request.getSemestre() != null) usuarioMateria.setSemestre(request.getSemestre());
        if (request.getAnio() != null) usuarioMateria.setAnio(request.getAnio());

        boolean estadoCambio = request.getEstado() != null && !request.getEstado().equals(usuarioMateria.getEstado());
        if (request.getEstado() != null) usuarioMateria.setEstado(request.getEstado());

        // Si se está cerrando, calcular la nota final real desde el servicio centralizado
        if (seEstaCerrando && estabaAbierta) {
            double notaReal = progressService.calcularNota(id);
            if (notaReal > 0) {
                usuarioMateria.setNotaFinal(notaReal);
            }
        } else if (request.getNotaFinal() != null && request.getNotaFinal() > 0) {
            usuarioMateria.setNotaFinal(request.getNotaFinal());
        }

        UsuarioMateriaResponse response = enrichWithProgress(
                usuarioMateriaMapper.toResponse(usuarioMateriaRepository.save(usuarioMateria)), id);

        if (estadoCambio) {
            progresoAcademicoService.recalcularProgreso(usuarioMateria.getUsuario().getId());
        }

        return response;
    }

    // ── Helpers ──

    /**
     * Enriquece el response con el avancePorcentaje calculado por MateriaProgressService.
     * Es la fuente única de verdad para el avance.
     */
    private UsuarioMateriaResponse enrichWithProgress(UsuarioMateriaResponse response, Long usuarioMateriaId) {
        double avance = progressService.calcularAvance(usuarioMateriaId);
        response.setAvancePorcentaje(Math.round(avance * 10.0) / 10.0);
        return response;
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        UsuarioMateria usuarioMateria = usuarioMateriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario-Materia no encontrada con id: " + id));
        usuarioMateriaRepository.delete(usuarioMateria);
    }
}
