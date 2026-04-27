package com.academicpath.backend.service;

import com.academicpath.backend.dto.request.RegistroRequest;
import com.academicpath.backend.dto.request.LoginRequest;
import com.academicpath.backend.dto.response.UsuarioResponse;
import com.academicpath.backend.dto.response.LoginResponse;

public interface AuthService {
    UsuarioResponse registro(RegistroRequest request);
    LoginResponse login(LoginRequest request);
}

