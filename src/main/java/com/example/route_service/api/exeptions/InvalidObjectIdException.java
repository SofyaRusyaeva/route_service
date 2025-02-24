package com.example.route_service.api.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidObjectIdException extends RuntimeException{
    public InvalidObjectIdException(String message) {super(message);}
}
