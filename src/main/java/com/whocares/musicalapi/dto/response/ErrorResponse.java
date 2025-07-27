package com.whocares.musicalapi.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorResponse {
    private String code;
    private String message;
    private LocalDateTime timestamp;
    private String path;

    public ErrorResponse(String code, String message, String path) {
        this.code = code;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.path = path;
    }

    public static ErrorResponse of(String code, String message, String path) {
        return new ErrorResponse(code, message, path);
    }
} 