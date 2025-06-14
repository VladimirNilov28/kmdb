package org.example.movesapi.service;

import org.example.movesapi.model.Genre;
import org.example.movesapi.repository.GenreRepository;
import org.springframework.stereotype.Service;

@Service
public class GenreService extends AbstractCRUDService<Genre, Long>{

    private GenreRepository repository;

    public GenreService(GenreRepository repository) {
        super(repository);
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
