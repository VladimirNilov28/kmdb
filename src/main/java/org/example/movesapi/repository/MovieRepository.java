package org.example.movesapi.repository;

import org.example.movesapi.model.Actor;
import org.example.movesapi.model.Genre;
import org.example.movesapi.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface MovieRepository extends JpaRepository<Movie, Long>{
    Page<Movie> findByGenresContaining(Genre genre, Pageable pageable);

    Page<Movie> findByReleaseYear(int releaseYear, Pageable pageable);

    Page<Movie> findByActorsContaining(Actor actor, Pageable pageable);

    /*
    SIMPLE JPQL QUERY:
    "SIZE(m.actors) + SIZE(m.genres)" calculates the total number of related actors and genres.
    If the sum is greater than 0, the query returns true; otherwise, it returns false.
    */

    @Query("""
        SELECT CASE
            WHEN (SIZE(m.actors) + SIZE(m.genres)) > 0
            THEN true ELSE false
        END
        FROM Movie m
        WHERE m.id = :movieId
    """)
    boolean isDependencyExist(@Param("movieId") Long movieId);

    List<Movie> id(Long id);
}
