package org.example.movesapi.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.movesapi.model.Actor;
import org.example.movesapi.model.Genre;
import org.example.movesapi.model.Movie;
import org.example.movesapi.repository.ActorRepository;
import org.example.movesapi.repository.GenreRepository;
import org.example.movesapi.repository.MovieRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;

@Component
@Profile("!test")
public class DataLoader implements CommandLineRunner {

    private final GenreRepository genreRepo;
    private final ActorRepository actorRepo;
    private final MovieRepository movieRepo;
    private final ObjectMapper mapper;

    public DataLoader(GenreRepository genreRepo,
                      ActorRepository actorRepo,
                      MovieRepository movieRepo,
                      ObjectMapper mapper) {
        this.genreRepo = genreRepo;
        this.actorRepo = actorRepo;
        this.movieRepo = movieRepo;
        this.mapper = mapper;
    }

    @Override
    public void run(String... args) throws Exception {
        if (movieRepo.count() > 0) {
            System.out.println("📦 Data is already loaded");
            return;
        }

        InputStream is = new ClassPathResource("sample_movies_data.json").getInputStream();
        JsonNode root = mapper.readTree(is);

        Map<Long, Genre> genreMap = new HashMap<>();
        for (JsonNode g : root.get("genres")) {
            long jsonId = g.get("id").asLong();
            Genre toSave = Genre.builder()
                    .name(g.get("name").asText())
                    .build();
            Genre saved = genreRepo.save(toSave);
            genreMap.put(jsonId, saved);
        }

        Map<Long, Actor> actorMap = new HashMap<>();
        for (JsonNode a : root.get("actors")) {
            long jsonId = a.get("id").asLong();
            Actor toSave = Actor.builder()
                    .name(a.get("name").asText())
                    .birthDate(LocalDate.parse(a.get("birthDate").asText()))
                    .build();
            Actor saved = actorRepo.save(toSave);
            actorMap.put(jsonId, saved);
        }

        // 3) Теперь создаём фильмы, подхватывая уже сохранённые объекты
        List<Movie> movies = new ArrayList<>();
        for (JsonNode m : root.get("movies")) {
            // подтягиваем Genre по JSON-id
            Set<Genre> genres = new HashSet<>();
            for (JsonNode gj : m.get("genres")) {
                genres.add(genreMap.get(gj.get("id").asLong()));
            }
            // подтягиваем Actor по JSON-id
            Set<Actor> actors = new HashSet<>();
            for (JsonNode aj : m.get("actors")) {
                actors.add(actorMap.get(aj.get("id").asLong()));
            }
            // строим и сохраняем Movie (без id)
            Movie movie = Movie.builder()
                    .name(m.get("name").asText())
                    .releaseYear(m.get("releaseYear").asInt())
                    .genres(genres)
                    .actors(actors)
                    .build();
            movies.add(movie);
        }
        movieRepo.saveAll(movies);

        System.out.println("✅ Loaded " + movies.size() + " movies.");
    }
}
