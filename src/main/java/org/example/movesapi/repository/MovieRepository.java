package org.example.movesapi.repository;

import org.example.movesapi.model.Actor;
import org.example.movesapi.model.Genre;
import org.example.movesapi.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface MovieRepository extends JpaRepository<Movie, Long>, PagingAndSortingRepository<Movie, Long> {

    Page<Movie> findByGenres(Set<Genre> genres,  Pageable pageable);

    Page<Movie> findByReleaseYear(int releaseYear, Pageable pageable);

    Page<Movie> findByActors(Set<Actor> actors, Pageable pageable);

    /**
     * Check whether a Movie has any Actor or Genre relationships.
     * ----------------------------------------------------------------------
     * This query selects from Movie and performs left joins on both the actors
     * and genres collections. It counts distinct Actor and Genre associations and
     * uses COALESCE to treat missing values as zero. The two counts are summed,
     * and a CASE expression returns true if the sum is greater than zero,
     * false otherwise. GROUP BY on the Movie ID ensures exactly one result per Movie.
     * ----------------------------------------------------------------------
     * @param movieId the primary key of the Movie to inspect
     * @return true if the Movie has at least one linked Actor or Genre;
     *         false if no associations exist or if the Movie does not exist
     */
    @Query("""
        SELECT CASE
            WHEN COALESCE(COUNT(DISTINCT a), 0) + COALESCE(COUNT(DISTINCT g), 0) > 0
            THEN true ELSE false
        END
        FROM Movie m
        LEFT JOIN m.actors a
        LEFT JOIN m.genres g
        WHERE m.id = :movieId
        GROUP BY m.id
    """)
    boolean isDependencyExist(@Param("movieId") Long movieId);

    /**
     * It is similar to previous, but it just counts sum of amount of dependencies
     * @param movieId the primary key of the Movie to check
     * @return count of dependencies
     */

    @Query("""
        SELECT COALESCE(COUNT(DISTINCT a), 0) + COALESCE(COUNT(DISTINCT g), 0)
        FROM Movie m
        LEFT JOIN m.actors a
        LEFT JOIN m.genres g
        WHERE m.id = :movieId
        GROUP BY m.id
    """)
    int getDependencyCount(@Param("movieId") Long movieId);

    List<Movie> id(Long id);

    Movie findByName(String name);
}
