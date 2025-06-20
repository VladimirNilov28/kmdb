package org.example.movesapi.exceptions;

import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler that converts Java and custom exceptions into proper HTTP responses.
 * <p>
 * This class is automatically picked up by Spring via {@link ControllerAdvice},
 * allowing consistent error handling across all REST controllers.
 */


@ControllerAdvice
public class GlobalExceptionHandler {


    /**
     * Handles validation errors from @Valid annotated request bodies.
     * Returns HTTP 400 Bad Request with a validation message.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    /**
     * Handles cases when a requested entity is not found in the database.
     * Returns HTTP 404 Not Found.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> handleNotFound() {
        return ResponseEntity.notFound().build();
    }

    /**
     * Handles unexpected null pointer exceptions.
     * Returns HTTP 404 Not Found (can be customized).
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Void> handleNullPointer() {
        return ResponseEntity.notFound().build();
    }

    /**
     * Handles cases when an entity cannot be deleted due to existing dependencies.
     * Returns HTTP 409 Conflict.
     */
    @ExceptionHandler(DependencyExistException.class)
    public ResponseEntity<String> handleDependencyExist(DependencyExistException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    /**
     * Handles illegal arguments passed to methods.
     * Returns HTTP 400 Bad Request with a message.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequest() {
        return ResponseEntity.badRequest().body("Bad Request");
    }
}
