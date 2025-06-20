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

import java.util.List;
import java.util.Set;


public interface MovieRepository extends JpaRepository<Movie, Long>, PagingAndSortingRepository<Movie, Long> {

    /**
     * Finds all movies that contain exactly the provided set of genres.
     * The use of GROUP BY and HAVING ensures that only movies containing
     * all specified genres (and only them) are returned.
     *
     * @param genres the set of genres to filter by
     * @param genreCount the number of genres provided (used for validation)
     * @param pageable pagination settings
     * @return a page of matching movies
     */
    @Query("""
        SELECT m FROM Movie m
        JOIN m.genres g
        WHERE g IN :genres
        GROUP BY m
        HAVING COUNT(DISTINCT g) = :genreCount
    """)
    Page<Movie> findByGenres(@Param("genres") Set<Genre> genres,
                             @Param("genreCount") long genreCount,
                             Pageable pageable);

    Page<Movie> findByReleaseYear(int releaseYear, Pageable pageable);

    /**
     * Finds all movies that contain exactly the provided set of actors.
     * Uses GROUP BY and HAVING to ensure exact matching of actor set.
     *
     * @param actors the set of actors to filter by
     * @param actorCount the number of actors provided (used for validation)
     * @param pageable pagination settings
     * @return a page of matching movies
     */
    @Query("""
        SELECT m FROM Movie m
        JOIN m.actors a
        WHERE a IN :actors
        GROUP BY m
        HAVING COUNT(DISTINCT a) = :actorCount
    """)
    Page<Movie> findByActors(@Param("actors") Set<Actor> actors,
                             @Param("actorCount") long actorCount,
                             Pageable pageable);

    /**
     * Check whether a Movie has any Actor or Genre relationships.
     * <p>
     * This query selects from Movie and performs left joins on both the actors
     * and genres collections. It counts distinct Actor and Genre associations and
     * uses COALESCE to treat missing values as zero. The two counts are summed,
     * and a CASE expression returns true if the sum is greater than zero,
     * false otherwise. GROUP BY on the Movie ID ensures exactly one result per Movie.
     * <p>
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
