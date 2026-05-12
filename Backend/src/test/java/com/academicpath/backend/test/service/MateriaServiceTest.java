package com.academicpath.backend.test.service;

import com.academicpath.backend.dto.request.MateriaRequest;
import com.academicpath.backend.dto.response.MateriaResponse;
import com.academicpath.backend.entity.Materia;
import com.academicpath.backend.exception.MateriaException;
import com.academicpath.backend.exception.ResourceNotFoundException;
import com.academicpath.backend.mapper.MateriaMapper;
import com.academicpath.backend.repository.MateriaRepository;
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
    private MateriaRepository materiaRepository;

    @Mock
    private MateriaMapper materiaMapper;

    @InjectMocks
    private MateriaServiceImpl materiaService;

    @Test
    public void testCrearMateriaExitoso() {
        MateriaRequest request = MateriaRequest.builder()
                .codigo("MAT101")
                .nombre("Cálculo I")
                .creditos(4)
                .descripcion("Introducción al cálculo")
                .build();

        Materia materiaGuardada = Materia.builder()
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

        when(materiaRepository.existsByCodigo(request.getCodigo())).thenReturn(false);
        when(materiaRepository.save(any())).thenReturn(materiaGuardada);
        when(materiaMapper.toResponse(materiaGuardada)).thenReturn(response);

        MateriaResponse result = materiaService.crear(request);

        assertNotNull(result);
        assertEquals(request.getCodigo(), result.getCodigo());
        verify(materiaRepository, times(1)).save(any());
    }

    @Test
    public void testCrearMateriaFallaCodigoExistente() {
        MateriaRequest request = MateriaRequest.builder()
                .codigo("MAT101")
                .nombre("Cálculo I")
                .creditos(4)
                .descripcion("Introducción al cálculo")
                .build();

        when(materiaRepository.existsByCodigo(request.getCodigo())).thenReturn(true);

        assertThrows(MateriaException.class, () -> materiaService.crear(request));
    }

    @Test
    public void testObtenerMateriaNoExiste() {
        when(materiaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> materiaService.obtenerPorId(1L));
    }
}
