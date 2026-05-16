package com.academicpath.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final org.slf4j.Logger log =
            org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), "Recurso no encontrado");
    }

    @ExceptionHandler(UsuarioException.class)
    public ResponseEntity<ErrorResponse> handleUsuarioException(UsuarioException ex, WebRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), "Error de usuario");
    }

    @ExceptionHandler(MateriaException.class)
    public ResponseEntity<ErrorResponse> handleMateriaException(MateriaException ex, WebRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), "Error de materia");
    }

    @ExceptionHandler(ActividadException.class)
    public ResponseEntity<ErrorResponse> handleActividadException(ActividadException ex, WebRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), "Error de actividad");
    }

    @ExceptionHandler(CalificacionException.class)
    public ResponseEntity<ErrorResponse> handleCalificacionException(CalificacionException ex, WebRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), "Error de calificación");
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException ex, WebRequest request) {
        return buildError(HttpStatus.UNAUTHORIZED, "Credenciales inválidas", "Autenticación fallida");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        return buildError(HttpStatus.FORBIDDEN, ex.getMessage(), "Acceso denegado");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> erroresCampos = ex.getBindingResult().getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Valor inválido",
                        (a, b) -> a
                ));

        String mensaje = erroresCampos.values().stream().findFirst()
                .orElse("Validación fallida");

        return buildError(HttpStatus.BAD_REQUEST, mensaje, "Error de validación");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        log.error("Error interno no controlado: {}", ex.getMessage(), ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error interno en el servidor", "Error interno");
    }

    private ResponseEntity<ErrorResponse> buildError(HttpStatus status, String message, String error) {
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .status(status.value())
                        .message(message)
                        .error(error)
                        .timestamp(System.currentTimeMillis())
                        .build(),
                status
        );
    }
}
