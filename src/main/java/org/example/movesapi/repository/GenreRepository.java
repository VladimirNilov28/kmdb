package org.example.movesapi.repository;

import org.example.movesapi.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {
}
