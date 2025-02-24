package com.example.route_service.api.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class RouteSaveException extends RuntimeException{
    public RouteSaveException(String message) {super(message);}
}
