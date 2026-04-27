package com.academicpath.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private int status;
    private String message;
    private String error;
    private long timestamp;
}

