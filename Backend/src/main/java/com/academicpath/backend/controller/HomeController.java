package com.academicpath.backend.controller;

import org.springframework.web.bind.annotation.RestController;

/**
 * HomeController deshabilitado.
 * Springdoc maneja el redirect a Swagger UI automáticamente.
 */
@RestController
public class HomeController {
    // Sin mappings — evita conflicto con SwaggerUiHome de springdoc
}
