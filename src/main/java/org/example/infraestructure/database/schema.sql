DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS ticket;

CREATE TABLE ticket (

    id BIGINT PRIMARY KEY,

    seat INT NOT NULL UNIQUE,

    status VARCHAR(20) NOT NULL

);

CREATE TABLE reservation (

    id BIGINT PRIMARY KEY,

    customer_id BIGINT NOT NULL,

    ticket_id BIGINT NOT NULL,

    reservation_date TIMESTAMP NOT NULL,

    CONSTRAINT fk_ticket
        FOREIGN KEY(ticket_id)
        REFERENCES ticket(id)

);