----DROP TABLE IF EXISTS CLIENT;
----CREATE SEQUENCE client_sequence START WITH 1 INCREMENT BY 1;
--
CREATE TABLE IF NOT EXISTS CLIENT (
    id BIGINT NOT NULL PRIMARY KEY,
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

CREATE TABLE IF NOT EXISTS MYSAMPLE(
    firstpool INT);
--
--DROP TABLE IF EXISTS ACCOUNT;

--CREATE TABLE IF NOT EXISTS ACCOUNT(
--    id BIGINT NOT NULL PRIMARY KEY,
--    account_number varchar(255) not null unique,
--    currency varchar(255) not null check (currency in ('PLN','USD','EUR', 'GBP', 'CHF', 'AUD', 'NOK')),
--    type varchar(255) not null check (type in ('CURRENT_ACCOUNT', 'SAVINGS_ACCOUNT')),
--    balance numeric(38,2) not null
----    client_id BIGINT NOT NULL,
----    FOREIGN KEY (client_id) REFERENCES CLIENT(id)
-- );
--
--
----DROP TABLE IF EXISTS TOKEN;
--
-- CREATE TABLE IF NOT EXISTS TOKEN (
--    id BIGINT NOT NULL PRIMARY KEY,
--    expired boolean not null,
--    revoked boolean not null,
--    client_id bigint NOT NULL,
--    token varchar(255) unique,
--    token_type varchar(255) check (token_type in ('BEARER')),
--    FOREIGN KEY (client_id) REFERENCES CLIENT(id)
-- );
--
----DROP TABLE IF EXISTS TRANSFER_HISTORY;
--
---- CREATE TABLE IF NOT EXISTS TRANSFER_HISTORY (
----    id INT  PRIMARY KEY,
----    amount numeric(38,2) not null,
----    balance numeric(38,2) not null,
----    before_balance numeric(38,2) not null,
------    client_id bigint not null,
----    created_by bigint,
----    created_on timestamp(6),
------    id INT not null,
----    update_on timestamp(6),
----    updated_by bigint,
----    account_number varchar(255) not null,
----    external_account_number varchar(255) not null,
----    local_date_time_pattern varchar(255),
----    title_of_transfer varchar(255) not null,
----    transfer_type varchar(255) not null check (transfer_type in ('INCOME','EXPENSE'))
---- );