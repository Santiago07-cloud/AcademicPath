package com.academicpath.backend.service.impl;

import com.academicpath.backend.dto.response.ProgresoAcademicoResponse;
import com.academicpath.backend.entity.Actividad;
import com.academicpath.backend.entity.Calificacion;
import com.academicpath.backend.entity.ProgresoAcademico;
import com.academicpath.backend.entity.Usuario;
import com.academicpath.backend.entity.UsuarioMateria;
import com.academicpath.backend.exception.ResourceNotFoundException;
import com.academicpath.backend.repository.ProgresoAcademicoRepository;
import com.academicpath.backend.repository.UsuarioMateriaRepository;
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
    private UsuarioMateriaRepository usuarioMateriaRepository;

    @Override
    @Transactional(readOnly = true)
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

        List<UsuarioMateria> todasLasMaterias = usuarioMateriaRepository.findByUsuarioId(usuarioId);

        int creditosTotales = 0;
        int creditosAprobados = 0;
        double promedioTotal = 0.0;

        for (UsuarioMateria usuarioMateria : todasLasMaterias) {
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

    @Override
    @Transactional
    public void inicializarProgreso(Usuario usuario) {
        ProgresoAcademico progreso = ProgresoAcademico.builder()
                .usuario(usuario)
                .creditosTotales(0)
                .creditosAprobados(0)
                .promedio(0.0)
                .fechaActualizacion(LocalDateTime.now())
                .build();
        progresoAcademicoRepository.save(progreso);
    }

    private double calcularPromedioPonderado(UsuarioMateria usuarioMateria) {
        List<Actividad> actividades = usuarioMateria.getActividades().stream().toList();

        if (actividades.isEmpty()) {
            return usuarioMateria.getNotaFinal() != null ? usuarioMateria.getNotaFinal() : 0.0;
        }

        double sumaPromedioPonderado = 0.0;
        double sumaPesos = 0.0;

        for (Actividad actividad : actividades) {
            List<Calificacion> calificaciones = actividad.getCalificaciones().stream().toList();

            if (!calificaciones.isEmpty()) {
                double promedio = calificaciones.stream()
                        .mapToDouble(Calificacion::getNota)
                        .average()
                        .orElse(0.0);

                sumaPromedioPonderado += promedio * actividad.getPeso();
                sumaPesos += actividad.getPeso();
            }
        }

        return sumaPesos > 0 ? sumaPromedioPonderado / sumaPesos : 0.0;
    }
}
