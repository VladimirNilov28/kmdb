package org.example.movesapi.controller;

import org.apache.coyote.BadRequestException;
import org.example.movesapi.model.Movie;
import org.example.movesapi.service.CRUDService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing {@link Movie} entities via the /movies endpoint.
 * <p>
 * Inherits all basic CRUD operations from {@link BaseController}.
 * <p>
 * Includes a custom search endpoint for retrieving a movie by its title.
 */


@RestController
@RequestMapping("/movies")
public class MovieController extends BaseController<Movie, Long> {

    public MovieController(CRUDService<Movie, Long> service) {
        super(service);
    }

    /**
     * Custom GET endpoint for retrieving a movie by its title.
     *
     * @param title the title of the movie to search for
     * @return the movie matching the title, if found
     * @throws BadRequestException if the title is invalid or not found
     */
    @GetMapping("/search")
    private ResponseEntity<Movie> search(@RequestParam String title) throws BadRequestException {
        return ResponseEntity.ok(service.findMovie(title));
    }
}
