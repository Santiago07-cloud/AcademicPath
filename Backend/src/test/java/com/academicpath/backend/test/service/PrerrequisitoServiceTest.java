package com.academicpath.backend.test.service;

import com.academicpath.backend.exception.ResourceNotFoundException;
import com.academicpath.backend.models.entity.Materias;
import com.academicpath.backend.models.entity.UsuarioMaterias;
import com.academicpath.backend.repository.MateriasRepository;
import com.academicpath.backend.repository.PrerrequsitosRepository;
import com.academicpath.backend.repository.UsuarioMateriasRepository;
import com.academicpath.backend.service.impl.PrerrequisitoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PrerrequisitoServiceTest {

    @Mock
    private PrerrequsitosRepository prerrequisitosRepository;

    @Mock
    private MateriasRepository materiasRepository;

    @Mock
    private UsuarioMateriasRepository usuarioMateriasRepository;

    @InjectMocks
    private PrerrequisitoServiceImpl prerrequisitosService;

    @Test
    public void testVerificarPrerrequisitosCompletosSinPrerrequisitos() {
        // Arrange
        when(prerrequisitosRepository.findByMateriaId(1L)).thenReturn(new ArrayList<>());

        // Act
        boolean result = prerrequisitosService.verificarPrerrequisitosCompletos(1L, 1L);

        // Assert
        assertTrue(result);
    }

    @Test
    public void testObtenerPrerrequisitosMateriaVacio() {
        // Arrange
        when(prerrequisitosRepository.findByMateriaId(1L)).thenReturn(new ArrayList<>());

        // Act
        List<Long> result = prerrequisitosService.obtenerPrerrequisitosMateria(1L);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    public void testCalcularMateriasDisponibles() {
        // Arrange
        Materias materia1 = Materias.builder().id(1L).codigo("MAT101").nombre("Cálculo I").creditos(4).build();
        
        List<Materias> todasMaterias = List.of(materia1);
        
        when(materiasRepository.findAll()).thenReturn(todasMaterias);
        when(prerrequisitosRepository.findByMateriaId(1L)).thenReturn(new ArrayList<>());
        when(usuarioMateriasRepository.findAprobadosByUsuarioId(1L)).thenReturn(new ArrayList<>());

        // Act
        Set<Long> result = prerrequisitosService.calcularMateriasDisponibles(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains(1L));
    }
}

