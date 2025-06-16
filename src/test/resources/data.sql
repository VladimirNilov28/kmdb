-- Жанры
INSERT INTO genre (id, genre) VALUES
                                 (1, 'Action'),
                                 (2, 'Drama'),
                                 (3, 'Sci-Fi'),
                                 (4, 'Thriller'),
                                 (5, 'Crime'),
                                 (6, 'Adventure');

-- Актёры
INSERT INTO actor (id, name, birth_date) VALUES
                                             (1, 'Keanu Reeves', '1964-09-02'),
                                             (2, 'Carrie-Anne Moss', '1967-08-21'),
                                             (3, 'Leonardo DiCaprio', '1974-11-11'),
                                             (4, 'Matthew McConaughey', '1969-11-04'),
                                             (5, 'Al Pacino', '1940-04-25'),
                                             (6, 'Christian Bale', '1974-01-30'),
                                             (7, 'Brad Pitt', '1963-12-18'),
                                             (8, 'Tom Hanks', '1956-07-09'),
                                             (9, 'Russell Crowe', '1964-04-07');

-- Фильмы
INSERT INTO movie (id, movie_name, release_year) VALUES
                                                     (1, 'The Matrix', 1999),
                                                     (2, 'Inception', 2010),
                                                     (3, 'Interstellar', 2014),
                                                     (4, 'John Wick', 2014),
                                                     (5, 'The Godfather', 1972),
                                                     (6, 'Pulp Fiction', 1994),
                                                     (7, 'The Dark Knight', 2008),
                                                     (8, 'Fight Club', 1999),
                                                     (9, 'Forrest Gump', 1994),
                                                     (10, 'Gladiator', 2000);

-- Связи Movie ↔ Actors
INSERT INTO movie_actors (movie_id, actor_id) VALUES
                                                  (1, 1), (1, 2),
                                                  (2, 3),
                                                  (3, 4),
                                                  (4, 1),
                                                  (5, 5),
                                                  (6, 5),
                                                  (7, 6),
                                                  (8, 7),
                                                  (9, 8),
                                                  (10, 9);

-- Связи Movie ↔ Genres
INSERT INTO movie_genres (movie_id, genre_id) VALUES
                                                  (1, 1),
                                                  (2, 3),
                                                  (3, 3),
                                                  (4, 1),
                                                  (5, 5),
                                                  (6, 5),
                                                  (7, 1),
                                                  (8, 4),
                                                  (9, 2),
                                                  (10, 6);
