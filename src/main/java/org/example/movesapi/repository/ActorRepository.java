package org.example.movesapi.repository;

import org.example.movesapi.model.Actor;
import org.example.movesapi.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface ActorRepository extends JpaRepository<Actor, Long>, PagingAndSortingRepository<Actor, Long> {
    Actor findByName(String name);
    /**
     * Check whether an Actor is linked to at least one Movie.
     * ----------------------------------------------------------------------
     * This query performs a left join between Actor and its movies collection.
     * It counts how many Movie entities are associated with the given Actor ID.
     * GROUP BY on the Actor ID ensures correct aggregation.
     * The CASE expression converts the count into a boolean result:
     * true if the count is greater than zero, false otherwise.
     * ----------------------------------------------------------------------
     * @param actorId the primary key of the Actor to check
     * @return true if there is at least one Movie linked to this Actor;
     *         false if no associations exist or if the Actor does not exist
     */
    @Query("""
        SELECT CASE
            WHEN COUNT(m) > 0
            THEN true ELSE false
        END
        FROM Actor a
        LEFT JOIN a.movies m
        WHERE a.id = :actorId
        GROUP BY a.id
        """)
    boolean isDependencyExists(@Param("actorId") Long actorId);

    @Query("""
        SELECT COUNT(m)
        FROM Actor a
        LEFT JOIN a.movies m
        WHERE a.id = :actorId
        GROUP BY a.id
        """)
    int getDependencyCount(@Param("actorId") Long actorId);

    Page<Actor> findByMovies(Set<Movie> movies, Pageable pageable);
}
