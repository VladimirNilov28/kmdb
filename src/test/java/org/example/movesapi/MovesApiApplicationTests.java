package org.example.movesapi;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.example.movesapi.model.Actor;
import org.example.movesapi.model.Genre;
import org.example.movesapi.model.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.net.URI;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
class MovesApiApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

//    @Test
//    void shouldReturnMovies() {
//        ResponseEntity<String> response = restTemplate
//                .withBasicAuth("admin", "admin")
//                .getForEntity("/movies", String.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//    }
//
//    @Test
//    void shouldReturnMovieByIdWith200andCorrectMovie() {
//        ResponseEntity<String> response = restTemplate
//                .withBasicAuth("admin", "admin")
//                .getForEntity("/movies/1", String.class);
//        System.out.println("BODY: " + response.getBody());
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//
//        DocumentContext documentContext = JsonPath.parse(response.getBody());
//
//        Integer id = documentContext.read("$.id");
//        assertThat(id).isEqualTo(1);
//
//        String movieName = documentContext.read("$.movieName");
//        assertThat(movieName).isEqualTo("The Matrix");
//    }
//
//    @Test
//    void shouldReturn404WhenGetNonexistentMovie() {
//        ResponseEntity<String> response = restTemplate
//                .withBasicAuth("admin", "admin")
//                .getForEntity("/movies/55", String.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//    }
//
//    @Test
//    @DirtiesContext
//    void shouldCreateMovieWith201andLocationHeader() {
//        Set<Genre> genres = Set.of(Genre.builder().id(1L).name("Test Genre").build());
//        Set<Actor> actors = Set.of(Actor.builder().id(1L).name("John").build());
//
//        Movie newMovie = Movie.builder()
//                .movieName("Test Movie")
//                .releaseYear(2004)
//                .genres(genres)
//                .actors(actors)
//                .build();
//
//        ResponseEntity<Void>  response = restTemplate
//                .withBasicAuth("admin", "admin")
//                .postForEntity("/movies", newMovie, Void.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//
//        URI location = response.getHeaders().getLocation();
//        ResponseEntity<String> getResponse = restTemplate
//                .withBasicAuth("admin", "admin")
//                .getForEntity(location, String.class);
//        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
//    }
//
//    @Test
//    void shouldRejectCreateMovieWithInvalidData() {
//        Set<Genre> genres = Set.of(Genre.builder().id(null).name("Test Genre").build());
//        Set<Actor> actors = Set.of(Actor.builder().id(null).name("").build());
//
//        Movie newMovie = Movie.builder()
//                .movieName("")
//                .releaseYear(2004)
//                .genres(genres)
//                .actors(actors)
//                .build();
//
//        ResponseEntity<Void> response = restTemplate
//                .withBasicAuth("admin", "admin")
//                .postForEntity("/movies", newMovie, Void.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
//    }
//
//    @Test
//    @DirtiesContext
//    void shouldPatchMoviePartiallyAndReturn200() {
//        // Given — создаём жанры и актёров (предполагаем, что они уже есть в БД с id=1)
//        Set<Genre> genres = Set.of(Genre.builder().id(1L).name("Test Genre").build());
//        Set<Actor> actors = Set.of(Actor.builder().id(1L).name("John").birthDate(LocalDate.of(1970, 1, 1)).build());
//
//        Movie originalMovie = Movie.builder()
//                .movieName("Original Movie")
//                .releaseYear(2000)
//                .genres(genres)
//                .actors(actors)
//                .build();
//
//        // When — создаём фильм
//        ResponseEntity<Void> postResponse = restTemplate
//                .withBasicAuth("admin", "admin")
//                .postForEntity("/movies", originalMovie, Void.class);
//        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//
//        URI location = postResponse.getHeaders().getLocation();
//        assertThat(location).isNotNull();
//
//        // And — обновляем только releaseYear
//        Map<String, Object> moviePatch = Map.of("releaseYear", 2020);
//        HttpEntity<Map<String, Object>> patchRequest = new HttpEntity<>(moviePatch);
//
//        ResponseEntity<Void> patchResponse = restTemplate
//                .withBasicAuth("admin", "admin")
//                .exchange(location, HttpMethod.PATCH, patchRequest, Void.class);
//        assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
//
//        // Then — проверяем, что только releaseYear обновился
//        ResponseEntity<Movie> getResponse = restTemplate
//                .withBasicAuth("admin", "admin")
//                .getForEntity(location, Movie.class);
//        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
//
//        Movie patchedMovie = getResponse.getBody();
//        assertThat(patchedMovie).isNotNull();
//        assertThat(patchedMovie.getReleaseYear()).isEqualTo(2020);
//        assertThat(patchedMovie.getMovieName()).isEqualTo("Original Movie"); // не должен измениться
//        assertThat(patchedMovie.getGenres()).extracting(Genre::getId).containsExactly(1L); // не должны пропасть
//        assertThat(patchedMovie.getActors()).extracting(Actor::getId).containsExactly(1L); // не должны пропасть
//    }
//
//    @Test
//    void shouldReturn404WhenPatchNonexistentMovie() {
//        Map<String,Object> movieUpdate = Map.of("releaseYear",2020);
//        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(movieUpdate);
//
//        ResponseEntity<Void> patchResponse = restTemplate
//                .withBasicAuth("admin", "admin")
//                .exchange("/movies/99", HttpMethod.PATCH, httpEntity, Void.class);
//        assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//    }
//
//    @Test
//    @DirtiesContext
//    void shouldDeleteMovieWith204() {
//        ResponseEntity<Void> response = restTemplate
//                .withBasicAuth("admin", "admin")
//                .exchange("/movies/1?force=true", HttpMethod.DELETE, null, Void.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
//
//        ResponseEntity<String> getResponse = restTemplate
//                .withBasicAuth("admin", "admin")
//                .getForEntity("/movies/1", String.class);
//        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//    }
//
//    //TODO тесть на удаление фильма с пустыми связями
//
//    @Test
//    void shouldNotDeleteMovieThatNotExist() {
//        ResponseEntity<Void> response = restTemplate
//                .withBasicAuth("admin", "admin")
//                .exchange("/movies/101", HttpMethod.DELETE, null, Void.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//    }
//
//    @Test
//    void shouldNotDeleteMovieWithDependencyAndReturnConflict() {
//        ResponseEntity<Void> repsonse = restTemplate
//                .withBasicAuth("admin", "admin")
//                .exchange("/movies/1", HttpMethod.DELETE, null, Void.class);
//        assertThat(repsonse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
//    }

    @Test
    void shouldFindMoviesByReleaseYear() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("admin", "admin")
                .getForEntity("")
    }

    //Actor TESTS

    @Test
    void shouldReturnActors() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("admin", "admin")
                .getForEntity("/actors", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldReturnActorById() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("admin", "admin")
                .getForEntity("/actors/1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        String name = documentContext.read("$.name", String.class);
        assertThat(name).isEqualTo("Keanu Reeves");
    }


}
