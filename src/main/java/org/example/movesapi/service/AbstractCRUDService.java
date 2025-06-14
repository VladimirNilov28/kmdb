package org.example.movesapi.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.movesapi.exceptions.DependencyExistException;
import org.example.movesapi.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Transactional
public abstract class AbstractCRUDService<T, ID> implements CRUDService<T, ID> {

    protected final JpaRepository<T, ID> repository;
    protected abstract ID getId(T entity);
    public AbstractCRUDService(JpaRepository<T, ID> repository) {
        this.repository = repository;
    }

    @Override
    public T create(T entity) {
        return repository.save(entity);
    }
    @Override
    public ID extractId(T entity) {
        try {
            return getId(entity);
        } catch (NullPointerException e) {
            throw new NullPointerException();
        }
    }

    @Override
    public T update(ID id, Map<String, Object> fields) {
        T entity = repository.findById(id).orElse(null);
        if (entity != null) {
            fields.forEach((key, value) -> {
                Field field = ReflectionUtils.findField(Movie.class, key);
                if (field != null) {
                    field.setAccessible(true);
                    ReflectionUtils.setField(field, entity, value);
                }
            });
            return repository.save(entity);
        } else {
            throw new EntityNotFoundException("Entity with id " + id + " not found");
        }
    }

    @Override
    public void delete(ID id, boolean force) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Movie not found: " + id);
        }
        if (force || !isDependencyExist(id)) {
            repository.deleteById(id);
        } else throw new DependencyExistException();
    }
    protected abstract boolean isDependencyExist(ID id);

    @Override
    public T getById(ID id) {
        T entity = repository.findById(id).orElse(null);
        if (entity != null) {
            return entity;
        } else {
            throw new EntityNotFoundException("Entity with id " + id + " not found");
        }
    }

    @Override
    public List<T> getAll() {
        try {
            return repository.findAll();
        } catch (NullPointerException e) {
            throw new NullPointerException();
        }
    }
}
