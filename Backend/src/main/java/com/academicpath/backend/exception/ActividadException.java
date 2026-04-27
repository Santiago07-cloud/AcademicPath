package com.academicpath.backend.exception;

public class ActividadException extends RuntimeException {
    
    public ActividadException(String message) {
        super(message);
    }

    public ActividadException(String message, Throwable cause) {
        super(message, cause);
    }
}

