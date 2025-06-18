package org.example.movesapi.exceptions;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> handleNotFound() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Void> handleNullPointer() {
        return ResponseEntity.notFound().build();
    }


    @ExceptionHandler(DependencyExistException.class)
    public ResponseEntity<String> handleDependencyExist(DependencyExistException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }



//    @ExceptionHandler(HttpMessageNotReadableException.class)
//    public ResponseEntity<Void> handleMalformedJson(HttpMessageNotReadableException ex) {
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//    }
//
//    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
//    public ResponseEntity<Void> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
//        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
//    }
//
//    @ExceptionHandler(NoHandlerFoundException.class)
//    public ResponseEntity<Void> handleNotFound(NoHandlerFoundException ex) {
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//    }
//    @ExceptionHandler(ConstraintViolationException.class)
//    @ExceptionHandler(IllegalArgumentException.class)
//    @ExceptionHandler(EntityNotFoundException.class)
}
