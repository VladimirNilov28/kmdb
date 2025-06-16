package org.example.movesapi.repository;

import org.example.movesapi.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface GenreRepository extends JpaRepository<Genre, Long>, PagingAndSortingRepository<Genre, Long> {
    Genre findByName(String name);


    /**
     * Determine whether a Genre is linked to at least one Movie.
     * ----------------------------------------------------------------------
     * This query performs a left join between Genre and its movies collection.
     * It counts the number of Movie entities associated with the given Genre ID.
     * A GROUP BY on the Genre ID ensures correct aggregation.
     * The CASE expression converts the count into a boolean result:
     * true if the count is greater than zero, false otherwise.
     * ----------------------------------------------------------------------
     * @param genreId the primary key of the Genre to check
     * @return true if there is at least one Movie linked to this Genre;
     *         false if no associations exist or if the Genre does not exist
     */
    @Query("""
        SELECT CASE 
                WHEN COUNT(m) > 0 
                THEN true ELSE false 
        END
        FROM Genre g
        LEFT JOIN g.movies m
        WHERE g.id = :genreId
        GROUP BY g.id
        """)
    boolean isDependencyExists(@Param("genreId") Long genreId);
}
