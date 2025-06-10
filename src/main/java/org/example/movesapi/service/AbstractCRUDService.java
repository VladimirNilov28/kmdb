package org.example.movesapi.service;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public abstract class AbstractCRUDService<T, ID> implements CRUDService<T, ID> {

    protected final JpaRepository<T, ID> repository;

    public AbstractCRUDService(JpaRepository<T, ID> repository) {
        this.repository = repository;
    }

    @Override
    public T create(T entity) {
        return repository.save(entity);
    }

    @Override
    public T update(ID id, T entity) {
        return null;
    }

    @Override
    public void delete(ID id) {
    }

    @Override
    public Optional<T> getById(ID id) {
        return null;
    }

    @Override
    public List<T> getAll() {
        return null;
    }
}
