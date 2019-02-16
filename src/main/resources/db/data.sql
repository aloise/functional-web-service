create table users(
    id SERIAL PRIMARY KEY,
    password CHAR(32) NOT NULL,
    email VARCHAR(64) NOT NULL UNIQUE
)