package org.example.movesapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Range;

import java.time.Year;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a movie with its basic data and relationships.
 * Contains information such as the movie name, release year, and many-to-many relationships
 * with {@code Genre} and {@code Actor} entities. This entity owns both relationships.
 * <p>
 * Uses Lombok to reduce boilerplate:
 * <ul>
 *   <li>{@code @Getter} – generates getters for all fields</li>
 *   <li>{@code @Setter} – generates setters for all fields</li>
 *   <li>{@code @NoArgsConstructor} – generates a no-args constructor</li>
 *   <li>{@code @AllArgsConstructor} – generates a constructor with all fields</li>
 *   <li>{@code @Builder} – provides a fluent builder API for object creation</li>
 *   <li>{@code @EqualsAndHashCode(of = "id")} – equality based only on ID</li>
 *   <li>{@code @ToString(exclude = {...})} – prevents recursive output in logs</li>
 * </ul>
 */


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
    private String name;

    @NotNull
    @Range(min = 1895, max = 2040)
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
