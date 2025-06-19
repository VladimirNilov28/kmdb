package org.example.movesapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a genre used to classify movies.
 * Contains basic data such as an ID and genre name, and maintains a many-to-many relationship with movies.
 * <p>
 * Uses Lombok to reduce boilerplate:
 * <ul>
 *   <li>{@code @Getter} – generates getters for all fields</li>
 *   <li>{@code @Setter} – generates setters for all fields</li>
 *   <li>{@code @NoArgsConstructor} – generates a no-args constructor</li>
 *   <li>{@code @AllArgsConstructor} – generates a constructor with all fields</li>
 *   <li>{@code @Builder} – provides a fluent builder API for object creation</li>
 *   <li>{@code @EqualsAndHashCode(of = "id")} – equality based only on ID</li>
 * </ul>
 */


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@Entity
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Genre name is required")
    private String name;

    @Builder.Default
    @ManyToMany(mappedBy = "genres")
    @JsonIgnore
    private Set<Movie> movies = new HashSet<>();
}
