package org.example.movesapi.service;

import org.example.movesapi.model.Actor;
import org.example.movesapi.repository.ActorRepository;
import org.springframework.stereotype.Service;

@Service
public class ActorService extends AbstractCRUDService<Actor, Long>{

    private final ActorRepository repository;

    public ActorService(ActorRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    protected boolean isDependencyExist(Long id) {
        return repository.isDependencyExists(id);
    }

    @Override
    protected Long getId(Actor entity) {
        return entity.getId();
    }
}
