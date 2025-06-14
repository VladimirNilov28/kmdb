INSERT INTO genre (id, name) VALUES (1, 'Action'), (2, 'Drama');

INSERT INTO actor (id, name, birth_date) VALUES
                                             (1, 'Keanu Reeves', '1964-09-02'),
                                             (2, 'Carrie-Anne Moss', '1967-08-21');

INSERT INTO movie (id, movie_name, release_year) VALUES
    (1, 'The Matrix', 1999);

-- ManyToMany связи (таблицы называются как: movie_actors, movie_genres)
INSERT INTO movie_actors (movie_id, actor_id) VALUES (1, 1), (1, 2);
INSERT INTO movie_genres (movie_id, genre_id) VALUES (1, 1);
