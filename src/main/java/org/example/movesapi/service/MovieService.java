package org.example.movesapi.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.movesapi.model.Actor;
import org.example.movesapi.model.Genre;
import org.example.movesapi.model.Movie;
import org.example.movesapi.repository.ActorRepository;
import org.example.movesapi.repository.GenreRepository;
import org.example.movesapi.repository.MovieRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class MovieService extends AbstractCRUDService<Movie, Long> {

    private final MovieRepository repository;
    private final GenreRepository genreRepository;
    private final ActorRepository actorRepository;
    private final MovieRepository movieRepository;

    public MovieService(MovieRepository repository, GenreRepository genreRepository, ActorRepository actorRepository, MovieRepository movieRepository) {
        super(repository);
        this.repository = repository;
        this.genreRepository = genreRepository;
        this.actorRepository = actorRepository;
        this.movieRepository = movieRepository;
    }

    @Override
    public Movie findMovie(String title) {
        Movie movie = repository.findByName(title);
        if(movie != null) {
            return movie;
        }
        throw new EntityNotFoundException("Movie with name " + title + " not found");
    }

    @Override
    protected String getName(Long id) {
        Optional<Movie> movie = repository.findById(id);
        if(movie.isPresent()) {
            return movie.get().getName();
        }
        throw new EntityNotFoundException("Movie not found");
    }

    @Override
    protected int getDependencyCount(Long id){
        return repository.getDependencyCount(id);
    }

    @Override
    protected boolean isDependencyExist(Long id) {
        return repository.isDependencyExist(id);
    }

    @Override
    protected Long getId(Movie entity) {
        return entity.getId();
    }

    //Filter impl for /movies?filter=...
    @Override
    protected Page<Movie> filter(String filter, Pageable pageable) {
        String[] parts = filter.split(":", 2);
        if (parts.length < 2 || parts[1].isBlank()) {
            throw new IllegalArgumentException("Filter must be in format key:value[,value...]");
        }
        String key = parts[0].trim();
        String value = parts[1].trim();
        return switch (key) {
            //Fetch all movie with provided genres
            case "genre" -> repository.findByGenres(getGenres(value), getPageable(pageable));

            //Fetch all move with provided release year
            case "releaseYear" -> repository.findByReleaseYear(Integer.parseInt(value), getPageable(pageable));

            //Find all movies with specific actors
            case "actor" -> repository.findByActors(getActors(value), getPageable(pageable));
            //find movies by set of actors
            default -> throw new IllegalArgumentException("Filter key:" + key + " not supported");
        };
    }



    private Set<Actor> getActors(String value) {
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .map(name -> Optional.ofNullable(actorRepository.findByName(name))
                        .orElseThrow(() -> new EntityNotFoundException("Actor not found:" + name)))
                .collect(Collectors.toSet());
    }

    private Set<Genre> getGenres(String value) {
        return Arrays.stream(value.split(","))
                .map(String::trim) //remove blank spaces
                .map(name -> Optional.ofNullable(genreRepository.findByName(name)) //finds genre in repo and checks for its existing
                        .orElseThrow(() -> new EntityNotFoundException("Genre not found:" + name)))
                .collect(Collectors.toSet());
    }

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
