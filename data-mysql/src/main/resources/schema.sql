CREATE TABLE IF NOT EXISTS book
(
    id          BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(255),
    description TEXT
);