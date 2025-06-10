package org.example.movesapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")       // только по ID
//@ToString(exclude = {"movies"})     // исключи рекурсивные поля

@Entity
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Genre name is required")
    private String genre;

    @ManyToMany(mappedBy = "genres")
    @JsonIgnore
    private Set<Movie> movies = new HashSet<>();
}
