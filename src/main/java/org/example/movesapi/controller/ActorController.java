package org.example.movesapi.controller;

import org.example.movesapi.model.Actor;
import org.example.movesapi.service.CRUDService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing {@link Actor} entities via the /actors endpoint.
 * <p>
 * Inherits all basic CRUD operations from {@link BaseController}.
 * <p>
 * You can override or extend methods here to add custom behavior specific to actors.
 */


@RestController
@RequestMapping("/actors")
public class ActorController extends BaseController<Actor, Long> {
    public ActorController(CRUDService<Actor, Long> service) {
        super(service);
    }

    // Place here any custom endpoints or overrides specific to actors
}
