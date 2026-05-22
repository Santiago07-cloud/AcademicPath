package com.academicpath.backend.service.impl;

import com.academicpath.backend.entity.Actividad;
import com.academicpath.backend.entity.Calificacion;
import com.academicpath.backend.entity.UsuarioMateria;
import com.academicpath.backend.exception.UsuarioException;
import com.academicpath.backend.repository.ActividadRepository;
import com.academicpath.backend.repository.CalificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio centralizado para calcular avance y validar cierre de materias.
 * Es la UNICA fuente de verdad para logica de progreso por materia.
 */
@Service
public class MateriaProgressService {

    @Autowired
    private ActividadRepository actividadRepository;

    @Autowired
    private CalificacionRepository calificacionRepository;

    /**
     * Calcula el porcentaje de avance real de una inscripcion.
     * Basado en peso acumulado de actividades que YA tienen calificacion.
     * Rango: 0.0 a 100.0
     */
    public double calcularAvance(Long usuarioMateriaId) {
        List<Actividad> actividades = actividadRepository.findByUsuarioMateriaId(usuarioMateriaId);
        if (actividades.isEmpty()) return 0.0;

        double pesoCalificado = 0.0;
        for (Actividad act : actividades) {
            List<Calificacion> cals = calificacionRepository.findByActividadId(act.getId());
            if (!cals.isEmpty()) {
                pesoCalificado += act.getPeso();
            }
        }

        return Math.min(pesoCalificado, 100.0);
    }

    /**
     * Calcula la nota actual ponderada de una inscripcion.
     * Solo considera actividades que tienen calificacion.
     * Escala 0.0 a 5.0.
     */
    public double calcularNota(Long usuarioMateriaId) {
        List<Actividad> actividades = actividadRepository.findByUsuarioMateriaId(usuarioMateriaId);
        if (actividades.isEmpty()) return 0.0;

        double pesoCalificado = 0.0;
        double notaPonderada  = 0.0;

        for (Actividad act : actividades) {
            List<Calificacion> cals = calificacionRepository.findByActividadId(act.getId());
            if (!cals.isEmpty()) {
                double nota = cals.get(0).getNota();
                notaPonderada += (nota / act.getNotaMaxima()) * act.getPeso();
                pesoCalificado += act.getPeso();
            }
        }

        if (pesoCalificado == 0) return 0.0;
        return Math.round(((notaPonderada / pesoCalificado) * 5.0) * 100.0) / 100.0;
    }

    /**
     * Valida que una materia puede cerrarse (avance == 100%).
     * Lanza UsuarioException si no cumple las condiciones.
     */
    public void validarCierreMateria(Long usuarioMateriaId, UsuarioMateria usuarioMateria) {
        String estado = usuarioMateria.getEstado();
        if (estado != null && (estado.equals("APROBADA") || estado.equals("REPROBADA"))) {
            throw new UsuarioException("La materia ya esta cerrada con estado: " + estado);
        }

        List<Actividad> actividades = actividadRepository.findByUsuarioMateriaId(usuarioMateriaId);

        if (actividades.isEmpty()) {
            throw new UsuarioException(
                "No se puede cerrar la materia: no tiene actividades registradas. " +
                "Agrega actividades y calificalas antes de cerrar."
            );
        }

        double pesoTotal = actividades.stream().mapToDouble(Actividad::getPeso).sum();
        if (pesoTotal < 100.0) {
            throw new UsuarioException(
                String.format(
                    "No se puede cerrar la materia: el peso total de actividades es %.1f%% (se requiere 100%%). " +
                    "Faltan %.1f%% por agregar.",
                    pesoTotal, 100.0 - pesoTotal
                )
            );
        }

        // Verificar que TODAS las actividades tienen calificacion
        List<String> sinCalificar = new java.util.ArrayList<>();
        for (Actividad act : actividades) {
            List<Calificacion> cals = calificacionRepository.findByActividadId(act.getId());
            if (cals.isEmpty()) {
                sinCalificar.add(act.getTitulo());
            }
        }

        if (!sinCalificar.isEmpty()) {
            throw new UsuarioException(
                "No se puede cerrar la materia: las siguientes actividades no tienen calificacion: " +
                String.join(", ", sinCalificar) + ". Calificalas antes de cerrar."
            );
        }
    }

    /**
     * Valida que la materia esta abierta (CURSANDO) para operaciones de escritura.
     */
    public void validarMateriaAbierta(UsuarioMateria usuarioMateria, String operacion) {
        String estado = usuarioMateria.getEstado();
        if (estado != null && (estado.equals("APROBADA") || estado.equals("REPROBADA"))) {
            throw new UsuarioException(
                "Operacion no permitida [" + operacion + "]: la materia ya esta cerrada (estado: " + estado + "). " +
                "Las materias finalizadas son de solo lectura."
            );
        }
    }
}
