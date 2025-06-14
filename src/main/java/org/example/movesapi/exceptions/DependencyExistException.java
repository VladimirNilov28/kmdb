package org.example.movesapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class DependencyExistException extends RuntimeException {
    public DependencyExistException() {
        super("Cannot delete: dependencies exist");
    }
}
