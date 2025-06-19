package org.example.movesapi.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.movesapi.model.Actor;
import org.example.movesapi.model.Genre;
import org.example.movesapi.model.Movie;
import org.example.movesapi.repository.ActorRepository;
import org.example.movesapi.repository.GenreRepository;
import org.example.movesapi.repository.MovieRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for handling Movie-specific logic.
 * <p>
 * Extends {@link AbstractCRUDService} to inherit generic CRUD operations.
 * Implements advanced filtering based on genres, actors, or release year.
 */
@Service
public class MovieService extends AbstractCRUDService<Movie, Long> {

    private final MovieRepository repository;
    private final GenreRepository genreRepository;
    private final ActorRepository actorRepository;
    private final MovieRepository movieRepository;

    /**
     * Constructs the MovieService with all required repositories.
     */
    public MovieService(MovieRepository repository, GenreRepository genreRepository, ActorRepository actorRepository, MovieRepository movieRepository) {
        super(repository);
        this.repository = repository;
        this.genreRepository = genreRepository;
        this.actorRepository = actorRepository;
        this.movieRepository = movieRepository;
    }

    /**
     * Finds a movie by its name.
     *
     * @param title the name of the movie to search for
     * @return the matching movie
     * @throws EntityNotFoundException if no movie is found with the given name
     */
    @Override
    public Movie findMovie(String title) {
        Movie movie = repository.findByName(title);
        if (movie != null) {
            return movie;
        }
        throw new EntityNotFoundException("Movie with name " + title + " not found");
    }

    /**
     * Retrieves the name of the movie by ID for error messages and logs.
     */
    @Override
    protected String getName(Long id) {
        return repository.findById(id)
                .map(Movie::getName)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found"));
    }

    /**
     * Returns the number of dependencies for a given movie (e.g. actors, genres).
     */
    @Override
    protected int getDependencyCount(Long id) {
        return repository.getDependencyCount(id);
    }

    /**
     * Checks if the movie has any associated dependencies.
     */
    @Override
    protected boolean isDependencyExist(Long id) {
        return repository.isDependencyExist(id);
    }

    /**
     * Returns the ID of the movie.
     */
    @Override
    protected Long getId(Movie entity) {
        return entity.getId();
    }

    /**
     * Supports filtering by:
     * - genre: e.g. /movies?filter=genre:Action,Comedy
     * - actor: e.g. /movies?filter=actor:Keanu Reeves
     * - releaseYear: e.g. /movies?filter=releaseYear:1999
     *
     * @param filter   the filter query in format key:value[,value]
     * @param pageable pagination and sorting parameters
     * @return filtered page of movies
     */
    @Override
    protected Page<Movie> filter(String filter, Pageable pageable) {
        String[] parts = filter.split(":", 2);
        if (parts.length < 2 || parts[1].isBlank()) {
            throw new IllegalArgumentException("Filter must be in format key:value[,value...]");
        }

        String key = parts[0].trim();
        String value = parts[1].trim();

        return switch (key) {
            case "genre" -> repository.findByGenres(getGenres(value), getPageable(pageable));
            case "releaseYear" -> repository.findByReleaseYear(Integer.parseInt(value), getPageable(pageable));
            case "actor" -> repository.findByActors(getActors(value), getPageable(pageable));
            default -> throw new IllegalArgumentException("Filter key: " + key + " not supported");
        };
    }

    /**
     * Retrieves a set of Actor entities by a comma-separated list of actor names.
     *
     * Example input: "Keanu Reeves, Carrie-Anne Moss"
     *
     * @param value comma-separated actor names
     * @return a Set of Actor entities from the database
     * @throws EntityNotFoundException if any of the names do not exist
     */
    private Set<Actor> getActors(String value) {
        return Arrays.stream(value.split(",")) // Split the string into ["Keanu Reeves", "Carrie-Anne Moss"]
                .map(String::trim) // Remove extra spaces from each name
                .map(name -> Optional.ofNullable(actorRepository.findByName(name)) // Try to find each actor by name
                        .orElseThrow(() -> new EntityNotFoundException("Actor not found: " + name))) // If not found, throw error
                .collect(Collectors.toSet()); // Collect all found actors into a Set<Actor>
    }

    /**
     * Retrieves a set of Genre entities by a comma-separated list of genre names.
     *
     * Example input: "Action, Comedy, Drama"
     *
     * @param value comma-separated genre names
     * @return a Set of Genre entities from the database
     * @throws EntityNotFoundException if any genre name is not found
     */
    private Set<Genre> getGenres(String value) {
        return Arrays.stream(value.split(",")) // Split input like "Action, Comedy" into ["Action", "Comedy"]
                .map(String::trim) // Trim spaces around genre names
                .map(name -> Optional.ofNullable(genreRepository.findByName(name)) // Look up each genre in the repository
                        .orElseThrow(() -> new EntityNotFoundException("Genre not found: " + name))) // Throw if not found
                .collect(Collectors.toSet()); // Collect results into a Set<Genre>
    }

    /**
     * Returns a new Pageable object with default sorting by name in ascending, case-insensitive order.
     * <p>
     * This method ensures that paginated queries are consistently sorted.
     *
     * @param pageable incoming pagination parameters
     * @return a Pageable with default sort fallback
     */
    private Pageable getPageable(Pageable pageable) {
        return PageRequest.of(
                pageable.getPageNumber(), // current page number
                pageable.getPageSize(),   // page size (how many records per page)
                pageable.getSortOr(Sort.by(
                        Sort.Order.asc("name").ignoreCase() // default sort: ascending by "name", case-insensitive
                ))
        );
    }
}

/*
    MovieService provides movie-specific logic layered on top of generic CRUD functionality.
    It supports complex filtering via query parameters, dependency checks, and safe deletions.

    The logic here connects entities like Actor and Genre to Movie,
    and handles transformation of query parameters into database operations.
*/
