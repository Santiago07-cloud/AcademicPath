package com.academicpath.backend.test.controller;

import com.academicpath.backend.controller.AuthController;
import com.academicpath.backend.dto.request.RegistroRequest;
import com.academicpath.backend.dto.response.UsuarioResponse;
import com.academicpath.backend.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testRegistroExitoso() throws Exception {
        RegistroRequest request = RegistroRequest.builder()
                .nombres("Juan")
                .apellidos("Pérez")
                .correo("juan@test.com")
                .contrasena("Password123!")
                .universidad("UdeA")
                .carrera("Ingeniería de Sistemas")
                .build();

        UsuarioResponse response = UsuarioResponse.builder()
                .id(1L)
                .nombres("Juan")
                .apellidos("Pérez")
                .correo("juan@test.com")
                .universidad("UdeA")
                .carrera("Ingeniería de Sistemas")
                .build();

        when(authService.registro(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.correo").value("juan@test.com"));
    }
}

