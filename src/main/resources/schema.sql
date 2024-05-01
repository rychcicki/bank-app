DROP TABLE IF EXISTS CLIENT;

CREATE TABLE CLIENT (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email varchar(255) NOT NULL,
    first_name varchar(255) NOT NULL,
    last_name varchar(255) NOT NULL,
    birth_date DATE NOT NULL,
    password varchar(255),
    role varchar(255) check (role in ('USER','ADMIN','MANAGER')),
    street_name varchar(255),
    street_number varchar(255),
    zip_code varchar(255),
    city varchar(255)
);
