package org.example.movesapi.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.movesapi.exceptions.DependencyExistException;
import org.example.movesapi.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;

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
    public void update(ID id, Map<String, Object> fields) {
        T entity = repository.findById(id).orElse(null);
        if (entity != null) {
            fields.forEach((key, value) -> {
                Field field = ReflectionUtils.findField(entity.getClass(), key);
                if (field != null) {
                    field.setAccessible(true);
                    ReflectionUtils.setField(field, entity, value);
                }
            });
            repository.save(entity);
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
        } else throw new DependencyExistException("Cannot delete genre" + getName(id) + "because it has" + getDependencyCount(id) + "associations");
    }
    protected abstract boolean isDependencyExist(ID id);
    protected abstract String getName(ID id);
    protected abstract int getDependencyCount(ID id);

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
    public Page<T> getAll(Pageable pageable, Optional<String> filter) {
        if (filter.isPresent()) {
            return filter(filter.get(), pageable);
        }
        return repository.findAll(PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSortOr(Sort.by(
                        Sort.Order.asc("name").ignoreCase()
                ))
        ));
    }
    protected abstract Page<T> filter(String filter, Pageable pageable);


}
