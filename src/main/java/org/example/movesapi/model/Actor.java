package org.example.movesapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing an actor who appears in one or more movies.
 * Contains basic personal data and a many-to-many relationship with movies.
 * <p>
 * Uses Lombok for reducing boilerplate:
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
@EqualsAndHashCode(of = "id")       // только по ID
@Entity
public class Actor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Actor name is required")
    private String name;

    @NotNull
    private LocalDate birthDate;

    @Builder.Default
    @ManyToMany(mappedBy = "actors")
    @JsonIgnore
    private Set<Movie> movies = new HashSet<>();
}
