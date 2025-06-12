package org.example.movesapi.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.example.movesapi.model.Movie;
import org.example.movesapi.service.CRUDService;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.List;
import java.util.Map;

public abstract class BaseController<T, ID> {

    protected final CRUDService<T, ID> service;

    public BaseController(CRUDService<T, ID> service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<T> create(@Valid @RequestBody T entity) {
        return ResponseEntity.ok(service.create(entity));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<T> update(@PathVariable ID id, @Valid @RequestBody Map<String, Object> entity) {
        return ResponseEntity.ok(service.update(id, entity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable ID id,
                                    @RequestParam(required = false ) boolean force) {
        service.delete(id, force);
        return ResponseEntity.noContent().build();
    }
}
