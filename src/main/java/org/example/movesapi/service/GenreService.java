package org.example.movesapi.service;

import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.example.movesapi.model.Genre;
import org.example.movesapi.repository.GenreRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class for handling Genre-specific logic.
 * <p>
 * Extends {@link AbstractCRUDService} to inherit generic CRUD operations.
 * <p>
 * Currently does not support filtering or searching by title.
 */
@Service
public class GenreService extends AbstractCRUDService<Genre, Long> {

    private final GenreRepository repository;

    /**
     * Constructs the GenreService with the provided repository.
     */
    public GenreService(GenreRepository repository) {
        super(repository);
        this.repository = repository;
    }

    /**
     * Searching by title is not supported for genres.
     *
     * @throws BadRequestException always
     */
    @Override
    public Genre findMovie(String title) throws BadRequestException {
        throw new BadRequestException("../genre/search is not supported");
    }

    /**
     * Returns the name of the genre for error messages and logging.
     *
     * @param id the genre ID
     * @return the genre name
     * @throws EntityNotFoundException if the genre is not found
     */
    @Override
    protected String getName(Long id) {
        return repository.findById(id)
                .map(Genre::getName)
                .orElseThrow(() -> new EntityNotFoundException("Genre with id " + id + " not found"));
    }

    /**
     * Returns the number of dependencies (e.g. linked movies) for the given genre.
     */
    @Override
    protected int getDependencyCount(Long genreId) {
        return repository.getDependencyCount(genreId);
    }

    /**
     * Filtering is not supported for genres.
     *
     * @throws IllegalArgumentException always
     */
    @Override
    protected Page<Genre> filter(String filter, Pageable pageable) {
        throw new IllegalArgumentException(".../genres does not support filtering");
    }

    /**
     * Checks if the genre has any existing dependencies.
     */
    @Override
    protected boolean isDependencyExist(Long id) {
        return repository.isDependencyExists(id);
    }

    /**
     * Returns the ID of the genre entity.
     */
    @Override
    protected Long getId(Genre entity) {
        return entity.getId();
    }
}

/*
    GenreService is responsible for managing Genre-specific operations.
    It inherits generic CRUD behavior from AbstractCRUDService,
    and provides concrete logic for safe deletion with dependency checks.

    Filtering and search are disabled for this service by design.
*/
