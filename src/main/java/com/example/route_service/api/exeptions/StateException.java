package com.example.route_service.api.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// TODO подумать какой лучше ResponseStatus (и исправить в GlobalExceptionHandler)

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class StateException extends RuntimeException {
    public StateException(String message) {
        super(message);
    }
}
