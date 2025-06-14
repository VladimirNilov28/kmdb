package org.example.movesapi.controller;

import org.example.movesapi.model.Genre;
import org.example.movesapi.service.CRUDService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/genres")
public class GenreController extends BaseController<Genre, Long> {
    public GenreController(CRUDService<Genre, Long> service) {
        super(service);
    }
}
