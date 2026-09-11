CREATE TABLE customers (
                           id         BIGSERIAL PRIMARY KEY,
                           unique_id  VARCHAR(50)  NOT NULL UNIQUE,
                           name       VARCHAR(150) NOT NULL,
                           email      VARCHAR(150) NOT NULL UNIQUE
);