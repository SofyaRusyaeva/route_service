package com.example.route_service.api.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ObjectSaveException extends RuntimeException {
    public ObjectSaveException(String message) {
        super(message);
    }
}
