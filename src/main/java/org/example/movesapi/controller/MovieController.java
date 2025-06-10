package org.example.movesapi.controller;


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
        //NullPointerException handling
        Optional<Movie> optional = movieRepository.findById(id);
        if (optional.isEmpty()) return ResponseEntity.notFound().build();

        Movie movie = optional.get();

        fields.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(Movie.class, key);
            //NullPointerException handling
            if (field != null) {
                field.setAccessible(true);
                try {
                    ReflectionUtils.setField(field, movie, value);
                } catch (Exception e) {
                    //TODO Logging
                }
            }
        });

        movieRepository.save(movie);
        return ResponseEntity.ok().build();
    }

//    @DeleteMapping("/{id}")
//    private ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
//        return null;
//    }

}
