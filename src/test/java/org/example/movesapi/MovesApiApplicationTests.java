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
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 📦 Integration Test Suite for the Movies API
 * <p>
 * Covers all REST functionality for:
 * <ul>
 *     <li>🎬 <b>Movies</b>: CRUD, filtering by genre/year/actor, search by title</li>
 *     <li>🧑‍🎤 <b>Actors</b>: CRUD, pagination, partial updates</li>
 *     <li>🎭 <b>Genres</b>: CRUD, deletion with dependency check</li>
 * </ul>
 *
 * 🧪 Features:
 * <ul>
 *     <li>Runs with {@code @SpringBootTest} to test the full context</li>
 *     <li>🔐 Authenticated via {@code admin/admin} in every request</li>
 *     <li>🔍 Uses {@link JsonPath} to parse and validate JSON responses</li>
 *     <li>♻️ Uses {@code @DirtiesContext} for isolated stateful tests</li>
 * </ul>
 *
 * Note: This class ensures that your API behaves as expected from an external consumer’s perspective.
 *
 * @author Vladimir
 * @see org.example.movesapi.model.Movie
 * @see org.example.movesapi.model.Actor
 * @see org.example.movesapi.model.Genre
 * @see org.springframework.boot.test.web.client.TestRestTemplate
 * @see org.junit.jupiter.api.Test
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
class MovesApiApplicationTests {



    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldReturnMovies() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("admin", "admin")
                .getForEntity("/movies", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldReturnMovieByIdWith200andCorrectMovie() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("admin", "admin")
                .getForEntity("/movies/1", String.class);
        System.out.println("BODY: " + response.getBody());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        Integer id = documentContext.read("$.id");
        assertThat(id).isEqualTo(1);

        String movieName = documentContext.read("$.name");
        assertThat(movieName).isEqualTo("The Matrix");
    }

    @Test
    void shouldReturn404WhenGetNonexistentMovie() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("admin", "admin")
                .getForEntity("/movies/55", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    void shouldCreateMovieWith201andLocationHeader() {
        Set<Genre> genres = Set.of(Genre.builder().id(1L).name("Test Genre").build());
        Set<Actor> actors = Set.of(Actor.builder().id(1L).name("John").build());

        Movie newMovie = Movie.builder()
                .name("Test Movie")
                .releaseYear(2004)
                .genres(genres)
                .actors(actors)
                .build();

        ResponseEntity<Void>  response = restTemplate
                .withBasicAuth("admin", "admin")
                .postForEntity("/movies", newMovie, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI location = response.getHeaders().getLocation();
        ResponseEntity<String> getResponse = restTemplate
                .withBasicAuth("admin", "admin")
                .getForEntity(location, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldRejectCreateMovieWithInvalidData() {
        Set<Genre> genres = Set.of(Genre.builder().id(null).name("Test Genre").build());
        Set<Actor> actors = Set.of(Actor.builder().id(null).name("").build());

        Movie newMovie = Movie.builder()
                .name("")
                .releaseYear(2004)
                .genres(genres)
                .actors(actors)
                .build();

        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("admin", "admin")
                .postForEntity("/movies", newMovie, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DirtiesContext
    void shouldPatchMoviePartiallyAndReturn200() {
        // Given — создаём жанры и актёров (предполагаем, что они уже есть в БД с id=1)
        Set<Genre> genres = Set.of(Genre.builder().id(1L).name("Test Genre").build());
        Set<Actor> actors = Set.of(Actor.builder().id(1L).name("John").birthDate(LocalDate.of(1970, 1, 1)).build());

        Movie originalMovie = Movie.builder()
                .name("Original Movie")
                .releaseYear(2000)
                .genres(genres)
                .actors(actors)
                .build();

        // When — создаём фильм
        ResponseEntity<Void> postResponse = restTemplate
                .withBasicAuth("admin", "admin")
                .postForEntity("/movies", originalMovie, Void.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI location = postResponse.getHeaders().getLocation();
        assertThat(location).isNotNull();

        // And — обновляем только releaseYear
        Map<String, Object> moviePatch = Map.of("releaseYear", 2020);
        HttpEntity<Map<String, Object>> patchRequest = new HttpEntity<>(moviePatch);

        ResponseEntity<Void> patchResponse = restTemplate
                .withBasicAuth("admin", "admin")
                .exchange(location, HttpMethod.PATCH, patchRequest, Void.class);
        assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Then — проверяем, что только releaseYear обновился
        ResponseEntity<Movie> getResponse = restTemplate
                .withBasicAuth("admin", "admin")
                .getForEntity(location, Movie.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        Movie patchedMovie = getResponse.getBody();
        assertThat(patchedMovie).isNotNull();
        assertThat(patchedMovie.getReleaseYear()).isEqualTo(2020);
        assertThat(patchedMovie.getName()).isEqualTo("Original Movie"); // не должен измениться
        assertThat(patchedMovie.getGenres()).extracting(Genre::getId).containsExactly(1L); // не должны пропасть
        assertThat(patchedMovie.getActors()).extracting(Actor::getId).containsExactly(1L); // не должны пропасть
    }

    @Test
    void shouldReturn404WhenPatchNonexistentMovie() {
        Map<String,Object> movieUpdate = Map.of("releaseYear",2020);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(movieUpdate);

        ResponseEntity<Void> patchResponse = restTemplate
                .withBasicAuth("admin", "admin")
                .exchange("/movies/99", HttpMethod.PATCH, httpEntity, Void.class);
        assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    void shouldDeleteMovieWith204() {
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("admin", "admin")
                .exchange("/movies/1?force=true", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate
                .withBasicAuth("admin", "admin")
                .getForEntity("/movies/1", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotDeleteMovieThatNotExist() {
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("admin", "admin")
                .exchange("/movies/101", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotDeleteMovieWithDependencyAndReturnConflict() {
        ResponseEntity<Void> repsonse = restTemplate
                .withBasicAuth("admin", "admin")
                .exchange("/movies/1", HttpMethod.DELETE, null, Void.class);
        assertThat(repsonse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

//    @Test
//    void shouldFindMoviesByReleaseYear() {
//        ResponseEntity<String> response = restTemplate
//                .withBasicAuth("admin", "admin")
//                .getForEntity("")
//    }

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

    @Test
    void shouldReturnAPageOfActors() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("admin", "admin")
                .getForEntity("/actors?page=0&size=3", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$[*]");
        assertThat(page.size()).isEqualTo(3);
    }

    @Test
    void shouldReturn404WhenGetNonexistentActor() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("admin", "admin")
                .getForEntity("/actors/101", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
    @Test
    void shouldFindMoviesByGenre() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("admin", "admin")
                .getForEntity("/movies?filter=genre:Crime", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext ctx = JsonPath.parse(response.getBody());
        List<Map<String, Object>> movies = ctx.read("$");
        assertThat(movies)
                .withFailMessage("At least one movie with Crime genre is required")
                .isNotEmpty();
        for (int i = 0; i < movies.size(); i++) {
            // Читаем список имён жанров у i-го фильма
            List<String> genreNames = ctx.read(String.format("$[%d].genres[*].name", i));
            assertThat(genreNames)
                    .withFailMessage("Фильм под индексом %d не содержит жанр Comedy: %s", i, genreNames)
                    .anyMatch(name -> name.equalsIgnoreCase("Crime"));
        }
    }

    @Test
    void shouldFindMoviesByReleaseYear() {
        // Фильтруем фильмы по году 1999
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("admin", "admin")
                .getForEntity("/movies?filter=releaseYear:1999", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext ctx = JsonPath.parse(response.getBody());
        List<Map<String, Object>> movies = ctx.read("$");
        assertThat(movies)
                .withFailMessage("Должен быть хотя бы один фильм за 1999 год")
                .isNotEmpty();

        for (int i = 0; i < movies.size(); i++) {
            Integer year = ctx.read(String.format("$[%d].releaseYear", i));
            assertThat(year)
                    .withFailMessage("Фильм под индексом %d имеет неверный год выпуска: %d", i, year)
                    .isEqualTo(1999);
        }
    }

    @Test
    void shouldFindMoviesByActor() {
        // Фильтруем фильмы по актёру Keanu Reeves
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("admin", "admin")
                .getForEntity("/movies?filter=actor:Keanu Reeves", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext ctx = JsonPath.parse(response.getBody());
        List<Map<String, Object>> movies = ctx.read("$");
        assertThat(movies)
                .withFailMessage("Должен быть хотя бы один фильм с Keanu Reeves")
                .isNotEmpty();

        for (int i = 0; i < movies.size(); i++) {
            List<String> actorNames = ctx.read(String.format("$[%d].actors[*].name", i));
            assertThat(actorNames)
                    .withFailMessage("Фильм под индексом %d не содержит актёра Keanu Reeves: %s", i, actorNames)
                    .anyMatch(name -> name.equalsIgnoreCase("Keanu Reeves"));
        }
    }


    @Test
    void shouldFindMovieUsingSearch() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("admin", "admin")
                .getForEntity("/movies/search?title=The Matrix", String.class);
    }

    // Actors

    @Test
    void shouldCreateActorWith201andLocationHeader() {
        Map<String, Object> actor = Map.of(
                "name", "Test Actor",
                "birthDate", "2000-01-01"
        );
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("admin", "admin")
                .postForEntity("/actors", actor, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation())
                .withFailMessage("Location header must be present")
                .isNotNull();
    }

    @Test
    void shouldRejectCreateActorWithInvalidData() {
        // Пустое имя и отсутствие даты рождения
        Map<String, Object> invalidActor = Map.of(
                "name", "",
                "birthDate", ""
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(invalidActor);

        ResponseEntity<String> response = restTemplate
                .withBasicAuth("admin", "admin")
                .postForEntity("/actors", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("name", "birthDate"); // если возвращается сообщение об ошибке
    }

    @Test
    @DirtiesContext
    void shouldPatchActorPartiallyAndReturn200() {
        // Обновляем имя существующего актёра с id = 1
        Map<String, Object> update = Map.of("name", "Updated Name");
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(update);

        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("admin", "admin")
                .exchange("/actors/1", HttpMethod.PATCH, entity, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Проверим, что имя действительно обновилось
        ResponseEntity<Actor> getResponse = restTemplate
                .withBasicAuth("admin", "admin")
                .getForEntity("/actors/1", Actor.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().getName()).isEqualTo("Updated Name");
    }


    @Test
    void shouldReturn404WhenPatchNonexistentActor() {
        Map<String, Object> update = Map.of("name", "No One");
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(update);

        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("admin", "admin")
                .exchange("/actors/9999", HttpMethod.PATCH, entity, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

@Test
void shouldDeleteActorWith204() {
    // 1. Создать временного актёра
    Map<String, Object> actor = Map.of(
            "name", "Temp Actor",
            "birthDate", "1990-01-01"
    );
    ResponseEntity<Void> createResponse = restTemplate
            .withBasicAuth("admin", "admin")
            .postForEntity("/actors", actor, Void.class);

    // 2. Вытащить ID из Location
    String location = createResponse.getHeaders().getLocation().toString();
    Long id = Long.valueOf(location.substring(location.lastIndexOf("/") + 1));

    // 3. Удалить
    ResponseEntity<Void> deleteResponse = restTemplate
            .withBasicAuth("admin", "admin")
            .exchange("/actors/" + id, HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);

    assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
}

    @Test
    void shouldReturn404WhenDeleteNonexistentActor() {
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("admin", "admin")
                .exchange("/actors/9999", HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldForceDeleteActorWith204() {
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("admin", "admin")
                .exchange("/actors/1?force=true", HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }


// Genres


    @Test
    void shouldReturnGenresListWhenExists() {
        // Предварительно заполнить жанры
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("admin", "admin")
                .getForEntity("/genres", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext ctx = JsonPath.parse(response.getBody());
        List<Map<String, Object>> list = ctx.read("$");
        assertThat(list)
                .withFailMessage("Genres list must not be empty")
                .isNotEmpty();
    }

    @Test
    void shouldReturnGenreByIdWith200andCorrectGenre() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("admin", "admin")
                .getForEntity("/genres/5", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext ctx = JsonPath.parse(response.getBody());
        String name = ctx.read("$.name");
        assertThat(name).isEqualTo("Crime");
    }

    @Test
    void shouldReturn404WhenGetNonexistentGenre() {
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("admin", "admin")
                .getForEntity("/genres/9999", Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldCreateGenreWith201andLocationHeader() {
        Map<String, Object> genre = Map.of("name", "NewGenre");
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("admin", "admin")
                .postForEntity("/genres", genre, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation())
                .withFailMessage("Location header must be present")
                .isNotNull();
    }

    @Test
    void shouldRejectCreateGenreWithInvalidData() {
        Map<String, Object> invalid = Map.of("name", "");
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("admin", "admin")
                .postForEntity("/genres", invalid, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldPatchGenrePartiallyAndReturn200() {
        // Обновляем название жанра с id = 3
        Map<String, Object> update = Map.of("name", "UpdatedGenre");
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(update);

        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("admin", "admin")
                .exchange("/genres/3", HttpMethod.PATCH, entity, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Проверим, что название действительно обновилось
        ResponseEntity<Genre> getResponse = restTemplate
                .withBasicAuth("admin", "admin")
                .getForEntity("/genres/3", Genre.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().getName()).isEqualTo("UpdatedGenre");
    }


    @Test
    void shouldReturn404WhenPatchNonexistentGenre() {
        Map<String, Object> update = Map.of("name", "NoGenre");
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(update);

        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("admin", "admin")
                .exchange("/genres/9999", HttpMethod.PATCH, entity, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldDeleteGenreWith204() {
        // 1. Создать временный жанр
        Map<String, Object> genre = Map.of("name", "TempGenre");
        ResponseEntity<Void> createResponse = restTemplate
                .withBasicAuth("admin", "admin")
                .postForEntity("/genres", genre, Void.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = createResponse.getHeaders().getLocation().toString();
        Long id = Long.valueOf(location.substring(location.lastIndexOf("/") + 1));

        // 2. Удалить
        ResponseEntity<Void> deleteResponse = restTemplate
                .withBasicAuth("admin", "admin")
                .exchange("/genres/" + id, HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // 3. Проверить, что жанр больше не существует
        ResponseEntity<String> getResponse = restTemplate
                .withBasicAuth("admin", "admin")
                .getForEntity("/genres/" + id, String.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturn404WhenDeleteNonexistentGenre() {
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("admin", "admin")
                .exchange("/genres/9999", HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldForceDeleteGenreWith204() {
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("admin", "admin")
                .exchange("/genres/5?force=true", HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }



}
