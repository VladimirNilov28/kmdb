package org.example.movesapi.controller;


import org.example.movesapi.model.Movie;
import org.example.movesapi.repository.MovieRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {
    private final MovieRepository movieRepository;

    private MovieController(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @GetMapping
    private ResponseEntity<List<Movie>> findAll() {
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    private ResponseEntity<Movie> findById(@PathVariable Long id) {
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    private ResponseEntity<Void> addMovie(@RequestBody Movie movie) {
        return null;
    }

    @PatchMapping("/{id}")
    private ResponseEntity<Void> updateMovie(@PathVariable Long id, @RequestBody Movie movie) {
        return null;
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        return null;
    }

}
