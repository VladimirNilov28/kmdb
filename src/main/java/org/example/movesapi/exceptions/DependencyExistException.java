package org.example.movesapi.exceptions;

public class DependencyExistException extends RuntimeException {
    public DependencyExistException(String message) {
        super(message);
    }
}
