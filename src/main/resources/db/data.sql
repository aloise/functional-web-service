create extension if not exists pgcrypto;

create table users(
    id SERIAL PRIMARY KEY,
    password VARCHAR(64) NOT NULL,
    email VARCHAR(64) NOT NULL UNIQUE
);

