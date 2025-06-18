package org.example.movesapi.service;

import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
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

@Service
public class ActorService extends AbstractCRUDService<Actor, Long>{

    private final ActorRepository repository;
    private final MovieRepository movieRepository;

    public ActorService(ActorRepository repository, MovieRepository movieRepository) {
        super(repository);
        this.repository = repository;
        this.movieRepository = movieRepository;
    }

    @Override
    public Actor findMovie(String title) throws BadRequestException {
        throw new BadRequestException("../actor/search is not supported");
    }

    @Override
    protected boolean isDependencyExist(Long id) {
        return repository.isDependencyExists(id);
    }

    @Override
    protected Long getId(Actor entity) {
        return entity.getId();
    }

    @Override
    protected String getName(Long id) {
        Optional<Actor> actor = repository.findById(id);
        if(actor.isPresent()) {
            return actor.get().getName();
        }
        throw new EntityNotFoundException("Actor not found");
    }

    @Override
    protected int getDependencyCount(Long id) {
        return repository.getDependencyCount(id);
    }

    //impl for filter /actors?filter=...
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

    private Set<Movie> getMovies(String value) {
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .map(name -> Optional.ofNullable(movieRepository.findByName(name))
                        .orElseThrow(() -> new EntityNotFoundException("Movie not found:" + name)))
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
