package org.example.movesapi.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.movesapi.exceptions.DependencyExistException;
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

/**
 * Abstract generic service that provides basic CRUD operations
 * and leaves entity-specific logic to be implemented by subclasses.
 *
 * @param <T>  the entity type
 * @param <ID> the type of the entity's identifier
 */
@Transactional
public abstract class AbstractCRUDService<T, ID> implements CRUDService<T, ID> {

    /**
     * JPA repository for performing basic database operations.
     */
    protected final JpaRepository<T, ID> repository;
    /**
     * Should return the ID of a given entity (used in extractId).
     */
    protected abstract ID getId(T entity);

    public AbstractCRUDService(JpaRepository<T, ID> repository) {
        this.repository = repository;
    }

    /**
     * Saves a new entity to the database.
     */
    @Override
    public T create(T entity) {
        entityValidator(entity);
        return repository.save(entity);
    }
    protected abstract void entityValidator(T entity);

    /**
     * Extracts the ID from a given entity using the implemented getId() method.
     */
    @Override
    public ID extractId(T entity) {
        try {
            return getId(entity);
        } catch (NullPointerException e) {
            throw new NullPointerException();
        }
    }

    /**
     * Partially updates an entity using reflection.
     * Fields from the given map are injected into the entity if they exist.
     *
     * @param id     the ID of the entity to update
     * @param fields a map of field names and their new values
     */
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

    /**
     * Deletes an entity by ID.
     * If `force` is false, it checks for existing dependencies before deletion.
     *
     * @throws DependencyExistException if entity has relations and force is false
     */
    @Override
    public void delete(ID id, boolean force) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Movie not found: " + id);
        }
        if (force || !isDependencyExist(id)) {
            repository.deleteById(id);
        } else throw new DependencyExistException("Cannot delete " + getName(id) + " because it has " + getDependencyCount(id) + " associations");
    }

    /**
     * Must be implemented to check if the entity has related dependencies (e.g. foreign keys).
     */
    protected abstract boolean isDependencyExist(ID id);
    /**
     * Must return how many dependencies are blocking deletion.
     */
    protected abstract String getName(ID id);
    /**
     * Finds an entity by ID or throws 404 if not found.
     */
    protected abstract int getDependencyCount(ID id);

    /**
     * Finds an entity by ID or throws 404 if not found.
     */
    @Override
    public T getById(ID id) {
        T entity = repository.findById(id).orElse(null);
        if (entity != null) {
            return entity;
        } else {
            throw new EntityNotFoundException("Entity with id " + id + " not found");
        }
    }

    /**
     * Returns a paginated list of all entities, optionally filtered by a string.
     * Sorting defaults to case-insensitive ascending by "name".
     */
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

    /**
     * Defines how the filtering logic should be applied (e.g. search by name).
     */
    protected abstract Page<T> filter(String filter, Pageable pageable);


}

/*
    This class serves as a reusable template for entity services such as MovieService, ActorService, and GenreService.

    It provides all common CRUD logic (create, getById, update, delete, getAll),
    while leaving entity-specific parts (getId, filter, getName, getDependencyCount, etc.)
    to be implemented in child classes.

    Used together with BaseController to avoid repetitive code in controllers and keep the architecture clean.
*/

