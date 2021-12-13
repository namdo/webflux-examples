CREATE TABLE IF NOT EXISTS book
(
    id          SERIAL PRIMARY KEY,
    title       VARCHAR(255),
    description VARCHAR(255)
);