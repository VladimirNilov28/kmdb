package org.example.movesapi.service;

import org.example.movesapi.model.Movie;
import org.example.movesapi.repository.MovieRepository;
import org.springframework.stereotype.Service;

@Service
public class MovieService extends AbstractCRUDService<Movie, Long> {
    public MovieService(MovieRepository repository) {
        super(repository);
    }

    @Override
    public void delete(Long id) {

    }

    /*
    TODO
        нужно реализовать DELETE для каждого контроллера,
        удали @Override для delete и ео тоже к хуям удали.
        реализуй отдельно контрллер и сервис для удаления
        используй isDependencyExist(id)
     */
}
