create table if not exists CLIENT (
    id bigint not null AUTO_INCREMENT PRIMARY KEY,
    first_name varchar(255) not null,
    last_name varchar(255) not null,
    email varchar(255) not null,
    birth_date date not null,
    password varchar(255),
    role varchar(255) check (role in ('USER','ADMIN','MANAGER')),
    street_name varchar(255),
    street_number varchar(255),
    zip_code varchar(255),
    city varchar(255)
);

CREATE TABLE IF NOT EXISTS ACCOUNT(
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    account_number varchar(255) not null /*unique*/,
    currency varchar(255) not null check (currency in ('PLN','USD','EUR', 'GBP', 'CHF', 'AUD', 'NOK')),
    type varchar(255) not null check (type in ('CURRENT_ACCOUNT', 'SAVINGS_ACCOUNT')),
    balance numeric(38,2) not null,
    client_id BIGINT NOT NULL,
    FOREIGN KEY (client_id) REFERENCES CLIENT(id)
 );

 CREATE TABLE IF NOT EXISTS TOKEN (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    expired boolean not null,
    revoked boolean not null,
    client_id bigint NOT NULL,
    token varchar(255) /*unique*/,
    token_type varchar(255) check (token_type in ('BEARER')),
    FOREIGN KEY (client_id) REFERENCES CLIENT(id)
 );

