package org.example.movesapi.controller;

import org.example.movesapi.model.Movie;
import org.example.movesapi.service.CRUDService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/movies")
public class MovieController extends BaseController<Movie, Long> {

    public MovieController(CRUDService<Movie, Long> service) {
        super(service);
    }


//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> delete(@PathVariable Long id,
//                                    @RequestParam(required = false ) boolean force) {
//        service.
//        return ResponseEntity.noContent().build();
//    }

//    @GetMapping
//    private ResponseEntity<List<Movie>> findAll() {
//        List<Movie> movies = movieRepository.findAll();
//        return ResponseEntity.ok(movies);
//    }
//
//    @GetMapping("/{id}")
//    private ResponseEntity<Movie> findById(@PathVariable Long id) {
//        Movie movie = movieRepository.findById(id).orElse(null);
//        if (movie == null) {
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.ok(movie);
//    }
//
//    @PostMapping
//    private ResponseEntity<Void> addMovie(@RequestBody @Valid Movie movie, UriComponentsBuilder ucb) {
//        Movie savedMovie = movieRepository.save(movie);
//        URI locationOfNewMovie = ucb
//                .path("/movies/{id}")
//                .buildAndExpand(savedMovie.getId())
//                .toUri();
//        return ResponseEntity.created(locationOfNewMovie).build();
//    }
//
//    @PatchMapping("/{id}")
//    private ResponseEntity<Void> updateMovie(@PathVariable Long id, @RequestBody Map<String, Object> fields) {
//        Movie movie = movieRepository.findById(id).orElse(null);
//
//        if (movie != null) {
//            fields.forEach((key, value) -> {
//                Field field = ReflectionUtils.findField(Movie.class, key);
//                if (field != null) {
//                    field.setAccessible(true);
//                    ReflectionUtils.setField(field, movie, value);
//                }
//            });
//            movieRepository.save(movie);
//            return ResponseEntity.ok().build();
//        }
//        throw new EntityNotFoundException("Movie with id " + id + " not found");
//    }
//
//    @DeleteMapping("/{id}")
//    private ResponseEntity<Void> deleteMovie(@PathVariable Long id,
//                                             @RequestParam(required = false ) boolean force) {
//        boolean deleteApprove = (force || movieRepository.isDependencyExist(id));
//        if (movieRepository.existsById(id)) {
//            movieRepository.deleteById(id);
//            return ResponseEntity.noContent().build();
//        }
//        return ResponseEntity.notFound().build();
//    }

}
