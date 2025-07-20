package com.example.route_service.api.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class StateException extends RuntimeException {
    public StateException(String message) {
        super(message);
    }
}
