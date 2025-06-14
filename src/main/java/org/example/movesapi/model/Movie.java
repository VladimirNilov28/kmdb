package org.example.movesapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")       // только по ID
@ToString(exclude = {"actors", "genres"})     // исключи рекурсивные поля

@Entity
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Move name is required")
    private String movieName;

    @NotNull
    private int releaseYear;

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "movie_genres",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")

    )
    @JsonIgnoreProperties("movies")
    private Set<Genre> genres = new HashSet<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "movie_actors",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id")
    )
    @JsonIgnoreProperties("movies")
    private Set<Actor> actors = new HashSet<>();
}
