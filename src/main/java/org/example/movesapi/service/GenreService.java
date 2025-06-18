package org.example.movesapi.service;

import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.example.movesapi.model.Actor;
import org.example.movesapi.model.Genre;
import org.example.movesapi.model.Movie;
import org.example.movesapi.repository.GenreRepository;
import org.example.movesapi.repository.MovieRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GenreService extends AbstractCRUDService<Genre, Long>{

    private final GenreRepository repository;

    public GenreService(GenreRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public Genre findMovie(String title) throws BadRequestException {
        throw new BadRequestException("../genre/search is not supported");
    }

    @Override
    protected String getName(Long id) {
        Optional<Genre> genre = repository.findById(id);
        if(genre.isPresent()) {
            return genre.get().getName();
        }
        throw new EntityNotFoundException("Genre with id " + id + " not found");
    }

    @Override
    protected int getDependencyCount(Long genreId) {
        return repository.getDependencyCount(genreId);
    }

    @Override
    protected Page<Genre> filter(String filter, Pageable pageable){
        throw new IllegalArgumentException(".../genres does not support filtering");
    }

    @Override
    protected boolean isDependencyExist(Long id) {
        return repository.isDependencyExists(id);
    }

    @Override
    protected Long getId(Genre entity) {
        return entity.getId();
    }
}
