package org.example.movesapi.repository;

import org.example.movesapi.model.Actor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ActorRepository extends JpaRepository<Actor, Long>{
    Actor findByName(String name);
}
