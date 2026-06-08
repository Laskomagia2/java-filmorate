DELETE FROM users_liked_film;
DELETE FROM user_relations;

DELETE FROM films;
DELETE FROM users;

DELETE FROM film_genres;
DELETE FROM film_ratings;
DELETE FROM user_relation_statuses;


ALTER TABLE films ALTER COLUMN id RESTART WITH 1;
ALTER TABLE users ALTER COLUMN id RESTART WITH 1;
ALTER TABLE user_relations ALTER COLUMN id RESTART WITH 1;


MERGE INTO film_genres (id, name) KEY(id) VALUES (1, 'Комедия');
MERGE INTO film_genres (id, name) KEY(id) VALUES (2, 'Драма');
MERGE INTO film_genres (id, name) KEY(id) VALUES (3, 'Мультфильм');
MERGE INTO film_genres (id, name) KEY(id) VALUES (4, 'Триллер');
MERGE INTO film_genres (id, name) KEY(id) VALUES (5, 'Документальный');
MERGE INTO film_genres (id, name) KEY(id) VALUES (6, 'Боевик');

-- Рейтинги (Обрати внимание на дефис в PG-13)
MERGE INTO film_ratings (id, name) KEY(id) VALUES (1, 'G');
MERGE INTO film_ratings (id, name) KEY(id) VALUES (2, 'PG');
MERGE INTO film_ratings (id, name) KEY(id) VALUES (3, 'PG-13');
MERGE INTO film_ratings (id, name) KEY(id) VALUES (4, 'R');
MERGE INTO film_ratings (id, name) KEY(id) VALUES (5, 'NC-17');

MERGE INTO user_relation_statuses (id, name) KEY(id) VALUES
    (1, 'Confirmed'),
    (2, 'Unconfirmed');