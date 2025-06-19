package org.example.movesapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom {@link RuntimeException} thrown when an entity has existing dependencies
 * that prevent it from being safely deleted.
 * <p>
 * Used in DELETE operations to indicate a conflict due to related data.
 */
@ResponseStatus(value = HttpStatus.CONFLICT)
public class DependencyExistException extends RuntimeException {
    public DependencyExistException(String message) {
        super(message);
    }
}
