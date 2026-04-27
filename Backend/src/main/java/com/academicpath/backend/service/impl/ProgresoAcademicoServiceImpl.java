package com.academicpath.backend.service.impl;

import com.academicpath.backend.dto.response.ProgresoAcademicoResponse;
import com.academicpath.backend.exception.ResourceNotFoundException;
import com.academicpath.backend.models.entity.Actividades;
import com.academicpath.backend.models.entity.Calificaciones;
import com.academicpath.backend.models.entity.ProgresoAcademico;
import com.academicpath.backend.models.entity.UsuarioMaterias;
import com.academicpath.backend.repository.ProgresoAcademicoRepository;
import com.academicpath.backend.repository.UsuarioMateriasRepository;
import com.academicpath.backend.service.ProgresoAcademicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProgresoAcademicoServiceImpl implements ProgresoAcademicoService {

    @Autowired
    private ProgresoAcademicoRepository progresoAcademicoRepository;

    @Autowired
    private UsuarioMateriasRepository usuarioMateriasRepository;

    @Override
    public ProgresoAcademicoResponse obtenerProgresoUsuario(Long usuarioId) {
        ProgresoAcademico progreso = progresoAcademicoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Progreso académico no encontrado para usuario: " + usuarioId));

        return ProgresoAcademicoResponse.builder()
                .id(progreso.getId())
                .usuarioId(progreso.getUsuario().getId())
                .creditosTotales(progreso.getCreditosTotales())
                .creditosAprobados(progreso.getCreditosAprobados())
                .promedio(progreso.getPromedio())
                .fechaActualizacion(progreso.getFechaActualizacion())
                .build();
    }

    @Override
    @Transactional
    public void recalcularProgreso(Long usuarioId) {
        ProgresoAcademico progreso = progresoAcademicoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Progreso académico no encontrado para usuario: " + usuarioId));

        List<UsuarioMaterias> todasLasMaterias = usuarioMateriasRepository.findByUsuarioId(usuarioId);
        
        int creditosTotales = 0;
        int creditosAprobados = 0;
        double promedioTotal = 0.0;

        for (UsuarioMaterias usuarioMateria : todasLasMaterias) {
            creditosTotales += usuarioMateria.getMateria().getCreditos();

            if ("APROBADO".equals(usuarioMateria.getEstado())) {
                creditosAprobados += usuarioMateria.getMateria().getCreditos();
                promedioTotal += calcularPromedioPonderado(usuarioMateria);
            }
        }

        double promedioPonderado = todasLasMaterias.isEmpty() ? 0.0 : promedioTotal / todasLasMaterias.size();

        progreso.setCreditosTotales(creditosTotales);
        progreso.setCreditosAprobados(creditosAprobados);
        progreso.setPromedio(Math.min(promedioPonderado, 5.0));
        progreso.setFechaActualizacion(LocalDateTime.now());

        progresoAcademicoRepository.save(progreso);
    }

    private double calcularPromedioPonderado(UsuarioMaterias usuarioMateria) {
        List<Actividades> actividades = usuarioMateria.getActividades().stream().toList();

        if (actividades.isEmpty()) {
            return usuarioMateria.getNotaFinal() != null ? usuarioMateria.getNotaFinal() : 0.0;
        }

        double sumaPromedioPonderado = 0.0;
        double sumaPesos = 0.0;

        for (Actividades actividad : actividades) {
            List<Calificaciones> calificaciones = actividad.getCalificaciones().stream().toList();

            if (!calificaciones.isEmpty()) {
                double promedio = calificaciones.stream()
                        .mapToDouble(Calificaciones::getNota)
                        .average()
                        .orElse(0.0);

                sumaPromedioPonderado += promedio * actividad.getPeso();
                sumaPesos += actividad.getPeso();
            }
        }

        return sumaPesos > 0 ? sumaPromedioPonderado / sumaPesos : 0.0;
    }
}



