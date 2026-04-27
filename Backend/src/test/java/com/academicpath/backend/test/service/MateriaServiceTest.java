package com.academicpath.backend.test.service;

import com.academicpath.backend.dto.request.MateriaRequest;
import com.academicpath.backend.dto.response.MateriaResponse;
import com.academicpath.backend.exception.MateriaException;
import com.academicpath.backend.exception.ResourceNotFoundException;
import com.academicpath.backend.mapper.MateriasMapper;
import com.academicpath.backend.models.entity.Materias;
import com.academicpath.backend.repository.MateriasRepository;
import com.academicpath.backend.service.impl.MateriaServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MateriaServiceTest {

    @Mock
    private MateriasRepository materiasRepository;

    @Mock
    private MateriasMapper materiasMapper;

    @InjectMocks
    private MateriaServiceImpl materiaService;

    @Test
    public void testCrearMateriaExitoso() {
        // Arrange
        MateriaRequest request = MateriaRequest.builder()
                .codigo("MAT101")
                .nombre("Cálculo I")
                .creditos(4)
                .descripcion("Introducción al cálculo")
                .build();

        Materias materiaGuardada = Materias.builder()
                .id(1L)
                .codigo(request.getCodigo())
                .nombre(request.getNombre())
                .creditos(request.getCreditos())
                .descripcion(request.getDescripcion())
                .build();

        MateriaResponse response = MateriaResponse.builder()
                .id(1L)
                .codigo(request.getCodigo())
                .nombre(request.getNombre())
                .creditos(request.getCreditos())
                .descripcion(request.getDescripcion())
                .build();

        when(materiasRepository.existsByCodigo(request.getCodigo())).thenReturn(false);
        when(materiasRepository.save(any())).thenReturn(materiaGuardada);
        when(materiasMapper.toResponse(materiaGuardada)).thenReturn(response);

        // Act
        MateriaResponse result = materiaService.crear(request);

        // Assert
        assertNotNull(result);
        assertEquals(request.getCodigo(), result.getCodigo());
        verify(materiasRepository, times(1)).save(any());
    }

    @Test
    public void testCrearMateriaFallaCodigoExistente() {
        // Arrange
        MateriaRequest request = MateriaRequest.builder()
                .codigo("MAT101")
                .nombre("Cálculo I")
                .creditos(4)
                .descripcion("Introducción al cálculo")
                .build();

        when(materiasRepository.existsByCodigo(request.getCodigo())).thenReturn(true);

        // Act & Assert
        assertThrows(MateriaException.class, () -> materiaService.crear(request));
    }

    @Test
    public void testObtenerMateriaNoExiste() {
        // Arrange
        when(materiasRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> materiaService.obtenerPorId(1L));
    }
}

