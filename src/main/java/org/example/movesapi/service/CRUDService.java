package org.example.movesapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Here is a interface for all our CRUD operations
 * It describes all our base methods
 * @param <T> Entity
 * @param <ID> ID of out entity
 */

public interface CRUDService<T, ID> {
    T create(T entity);
    T update(ID id, Map<String, Object> entity);
    void delete(ID id, boolean force);
    T getById(ID id);
    Page<T> getAll(Pageable pageable, Optional<List<String>> filter);
    ID extractId(T entity);
}
