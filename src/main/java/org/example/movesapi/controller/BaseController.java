package org.example.movesapi.controller;

import jakarta.validation.Valid;
import org.example.movesapi.service.CRUDService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class BaseController<T, ID> {

    protected final CRUDService<T, ID> service;
    //protected abstract ID getId(T entity);

    public BaseController(CRUDService<T, ID> service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid T entity) {
        T newEntity = service.create(entity);
        ID id = service.extractId(newEntity);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return  ResponseEntity.created(location).build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable ID id, @RequestBody @Valid Map<String, Object> entity) {
        service.update(id, entity);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable ID id,
                                    @RequestParam(name = "force", required = false, defaultValue = "false") Boolean force) {
        service.delete(id, force);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<T> findById(@PathVariable ID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<T>> getAll(Pageable pageable, @RequestParam Optional<String> filter) {
        return ResponseEntity.ok(service.getAll(pageable, filter).getContent());
    }


}
