package org.example.movesapi.service;

import org.example.movesapi.exceptions.DependencyExistException;
import org.example.movesapi.model.Movie;
import org.example.movesapi.repository.MovieRepository;
import org.springframework.stereotype.Service;

@Service
public class MovieService extends AbstractCRUDService<Movie, Long> {

    private final MovieRepository repository;

    public MovieService(MovieRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    protected boolean isDependencyExist(Long id) {
        return repository.isDependencyExist(id);
    }

    @Override
    protected Long getId(Movie entity) {
        return entity.getId();
    }


    /*
    TODO
        нужно реализовать DELETE для каждого контроллера,
        удали @Override для delete и ео тоже к хуям удали.
        реализуй отдельно контроллер и сервис для удаления
        используй isDependencyExist(id)
     */
}
