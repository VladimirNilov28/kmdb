package org.example.movesapi.repository;

import org.example.movesapi.model.Actor;
import org.example.movesapi.model.Genre;
import org.example.movesapi.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long>{
    Page<Movie> findByGenresContaining(Genre genre, Pageable pageable);

    Page<Movie> findByReleaseYear(int releaseYear, Pageable pageable);

    Page<Movie> findByActorsContaining(Actor actor, Pageable pageable);
}
