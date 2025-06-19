package org.example.movesapi.service;

import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.example.movesapi.model.Actor;
import org.example.movesapi.model.Movie;
import org.example.movesapi.repository.ActorRepository;
import org.example.movesapi.repository.MovieRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for handling Actor-specific logic.
 * <p>
 * Extends {@link AbstractCRUDService} to inherit generic CRUD operations,
 * and implements custom filtering and deletion checks specific to Actor entities.
 */

@Service
public class ActorService extends AbstractCRUDService<Actor, Long>{

    private final ActorRepository repository;
    private final MovieRepository movieRepository;

    /**
     * Constructs the ActorService with repositories for actor and movie data.
     */
    public ActorService(ActorRepository repository, MovieRepository movieRepository) {
        super(repository);
        this.repository = repository;
        this.movieRepository = movieRepository;
    }

    /**
     * Actor search by movie title is not supported in this service.
     * Always throws BadRequestException.
     */
    @Override
    public Actor findMovie(String title) throws BadRequestException {
        throw new BadRequestException("../actor/search is not supported");
    }

    /**
     * Checks if the actor has any existing dependencies (e.g., linked movies).
     */
    @Override
    protected boolean isDependencyExist(Long id) {
        return repository.isDependencyExists(id);
    }

    /**
     * Returns the ID of the actor entity.
     */
    @Override
    protected Long getId(Actor entity) {
        return entity.getId();
    }

    /**
     * Returns the actor's name for display in dependency-related exceptions.
     *
     * @throws EntityNotFoundException if the actor is not found
     */
    @Override
    protected String getName(Long id) {
        Optional<Actor> actor = repository.findById(id);
        if(actor.isPresent()) {
            return actor.get().getName();
        }
        throw new EntityNotFoundException("Actor not found");
    }

    /**
     * Returns the number of dependencies the actor has (e.g. movie associations).
     */
    @Override
    protected int getDependencyCount(Long id) {
        return repository.getDependencyCount(id);
    }

    /**
     * Handles custom filtering for actor entities.
     * <p>
     * Currently supports filtering by movie title(s):
     * <pre>
     * /actors?filter=movie:Inception,Matrix
     * </pre>
     *
     * @param filter   filter string in the format key:value[,value]
     * @param pageable pagination and sorting information
     * @return paginated list of matching actors
     * @throws IllegalArgumentException for unsupported or malformed filters
     */
    @Override
    protected Page<Actor> filter(String filter, Pageable pageable) {
        String[] parts = filter.split(":", 2);
        if (parts.length < 2 || parts[1].isBlank()) {
            throw new IllegalArgumentException("Filter must be in format key:value[,value...]");
        }
        String key = parts[0].trim();
        String value = parts[1].trim();
        //Find all actors in specific movie
        if (key.equals("movie")) {
            return repository.findByMovies(getMovies(value), getPageable(pageable));
        }
        throw new IllegalArgumentException("Filter key:" + key + " not supported");
    }

    /**
     * Parses a comma-separated string of movie names
     * and retrieves corresponding Movie entities from the database.
     *
     * @param value comma-separated movie names
     * @return a set of matching Movie entities
     * @throws EntityNotFoundException if any movie is not found
     */
    private Set<Movie> getMovies(String value) {
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .map(name -> Optional.ofNullable(movieRepository.findByName(name))
                        .orElseThrow(() -> new EntityNotFoundException("Movie not found:" + name)))
                .collect(Collectors.toSet());
    }

    /**
     * Returns a Pageable object with default sorting by name (case-insensitive).
     * Used to ensure consistent pagination in filtered queries.
     */
    private Pageable getPageable(Pageable pageable) {
        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSortOr(Sort.by(
                        Sort.Order.asc("name").ignoreCase()
                ))
        );
    }
}

/*
    This service handles all actor-related business logic,
    such as safe deletion with dependency checks and advanced filtering by movie title.

    It relies on AbstractCRUDService to provide standard CRUD functionality,
    and focuses only on logic specific to Actor entities.
 */

