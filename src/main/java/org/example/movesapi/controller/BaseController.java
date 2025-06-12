package org.example.movesapi.controller;

import jakarta.validation.Valid;
import org.example.movesapi.service.CRUDService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

public abstract class BaseController<T, ID> {

    protected final CRUDService<T, ID> service;
    protected abstract ID getId(T entity);

    public BaseController(CRUDService<T, ID> service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid T entity, UriComponentsBuilder uri) {
        T newEntity = service.create(entity);
        return uri
                .path("/{id}")
                .buildAndExpand(getId(newEntity))
                .toUri();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable ID id, @RequestBody @Valid Map<String, Object> entity) {
        service.update(id, entity);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable ID id,
                                    @RequestParam(required = false ) Boolean force) {
        service.delete(id, force);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<T> findById(@PathVariable ID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<T>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }


}
