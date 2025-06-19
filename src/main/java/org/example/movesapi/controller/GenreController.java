package org.example.movesapi.controller;

import org.example.movesapi.model.Genre;
import org.example.movesapi.service.CRUDService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing {@link Genre} entities via the /genres endpoint.
 * <p>
 * Inherits all basic CRUD operations from {@link BaseController}.
 * <p>
 * You can extend this class to add custom behavior specific to genres.
 */

@RestController
@RequestMapping("/genres")
public class GenreController extends BaseController<Genre, Long> {
    public GenreController(CRUDService<Genre, Long> service) {
        super(service);
    }

    // Extend this controller with custom genre-specific endpoints if needed
}
