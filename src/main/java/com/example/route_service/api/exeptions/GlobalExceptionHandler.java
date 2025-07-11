package com.example.route_service.api.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleRouteNotFoundException(ObjectNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of(
                        "status", HttpStatus.NOT_FOUND.value(),
                        "message", e.getMessage()
                )
        );
    }

    @ExceptionHandler(InvalidObjectIdException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidObjectIdException(InvalidObjectIdException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of(
                        "status", HttpStatus.BAD_REQUEST.value(),
                        "message", e.getMessage()
                )
        );
    }

    @ExceptionHandler(ObjectSaveException.class)
    public ResponseEntity<Map<String, Object>> handleRouteSaveException(ObjectSaveException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Map.of(
                        "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "message", e.getMessage()
                )
        );
    }

    //
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<Map<String, Object>> handleGenericException(Exception e) {

    /// /        log.error("Unhandled exception occurred", e);
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
//                Map.of(
//                        "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                        "message", "Internal error"
//                )
//        );
//    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of(
                        "status", HttpStatus.BAD_REQUEST.value(),
                        "errors", errors
                )
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of(
                        "status", HttpStatus.UNAUTHORIZED.value(),
                        "message", "Authentication required"
                )
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                Map.of(
                        "status", HttpStatus.FORBIDDEN.value(),
                        "message", "Access denied"
                )
        );
    }

    @ExceptionHandler(StateException.class)
    public ResponseEntity<?> handleStateException(StateException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of(
                        "status", HttpStatus.BAD_REQUEST.value(),
                        "message", e.getMessage()
                )
        );
    }
}