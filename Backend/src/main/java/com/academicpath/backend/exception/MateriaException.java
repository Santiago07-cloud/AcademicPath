package com.academicpath.backend.exception;

public class MateriaException extends RuntimeException {
    
    public MateriaException(String message) {
        super(message);
    }

    public MateriaException(String message, Throwable cause) {
        super(message, cause);
    }
}

