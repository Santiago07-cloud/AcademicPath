package com.academicpath.backend.test.service;

import com.academicpath.backend.entity.Materia;
import com.academicpath.backend.entity.UsuarioMateria;
import com.academicpath.backend.repository.MateriaRepository;
import com.academicpath.backend.repository.PrerrequisitosRepository;
import com.academicpath.backend.repository.UsuarioMateriaRepository;
import com.academicpath.backend.service.impl.PrerrequisitoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PrerrequisitoServiceTest {

    @Mock
    private PrerrequisitosRepository prerrequisitosRepository;

    @Mock
    private MateriaRepository materiaRepository;

    @Mock
    private UsuarioMateriaRepository usuarioMateriaRepository;

    @InjectMocks
    private PrerrequisitoServiceImpl prerrequisitosService;

    @Test
    public void testVerificarPrerrequisitosCompletosSinPrerrequisitos() {
        when(prerrequisitosRepository.findByMateriaId(1L)).thenReturn(new ArrayList<>());

        boolean result = prerrequisitosService.verificarPrerrequisitosCompletos(1L, 1L);

        assertTrue(result);
    }

    @Test
    public void testObtenerPrerrequisitosMateriaVacio() {
        when(prerrequisitosRepository.findByMateriaId(1L)).thenReturn(new ArrayList<>());

        List<Long> result = prerrequisitosService.obtenerPrerrequisitosMateria(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testCalcularMateriasDisponibles() {
        Materia materia1 = Materia.builder()
                .id(1L)
                .codigo("MAT101")
                .nombre("Cálculo I")
                .creditos(4)
                .build();

        when(materiaRepository.findAll()).thenReturn(List.of(materia1));
        when(prerrequisitosRepository.findByMateriaId(1L)).thenReturn(new ArrayList<>());
        when(usuarioMateriaRepository.findAprobadosByUsuarioId(1L)).thenReturn(new ArrayList<>());

        Set<Long> result = prerrequisitosService.calcularMateriasDisponibles(1L);

        assertNotNull(result);
        assertTrue(result.contains(1L));
    }
}
