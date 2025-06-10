package org.example.movesapi.controller;


import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.example.movesapi.model.Movie;
import org.example.movesapi.repository.MovieRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/movies")
public class MovieController {
    private final MovieRepository movieRepository;

    private MovieController(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @GetMapping
    private ResponseEntity<List<Movie>> findAll() {
        List<Movie> movies = movieRepository.findAll();
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/{id}")
    private ResponseEntity<Movie> findById(@PathVariable Long id) {
        Movie movie = movieRepository.findById(id).orElse(null);
        if (movie == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(movie);
    }

    @PostMapping
    private ResponseEntity<Void> addMovie(@RequestBody @Valid Movie movie, UriComponentsBuilder ucb) {
        Movie savedMovie = movieRepository.save(movie);
        URI locationOfNewMovie = ucb
                .path("/movies/{id}")
                .buildAndExpand(savedMovie.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewMovie).build();
    }

    @PatchMapping("/{id}")
    private ResponseEntity<Void> updateMovie(@PathVariable Long id, @RequestBody Map<String, Object> fields) {
        Movie movie = movieRepository.findById(id).orElse(null);

        if (movie != null) {
            fields.forEach((key, value) -> {
                Field field = ReflectionUtils.findField(Movie.class, key);
                if (field != null) {
                    field.setAccessible(true);
                    ReflectionUtils.setField(field, movie, value);
                }
            });
            movieRepository.save(movie);
            return ResponseEntity.ok().build();
        }
        throw new EntityNotFoundException("Movie with id " + id + " not found");
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteMovie(@PathVariable Long id) {

        if (movieRepository.existsById(id)) {
            movieRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}
