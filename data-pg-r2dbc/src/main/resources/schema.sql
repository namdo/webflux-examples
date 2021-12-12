DROP TABLE IF EXISTS "book";

CREATE TABLE book
(
    id          SERIAL PRIMARY KEY,
    title       VARCHAR(255),
    description TEXT
);