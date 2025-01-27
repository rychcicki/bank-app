CREATE SEQUENCE IF NOT EXISTS account_sequence START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS client_sequence START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS history_generator START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS token_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS CLIENT (
	birth_date date NOT NULL,
	created_by int8 NULL,
	created_on timestamp(6) NULL,
	id int8  NOT NULL DEFAULT NEXT VALUE FOR client_sequence PRIMARY KEY,
	update_on timestamp(6) NULL,
	updated_by int8 NULL,
	city varchar(255) NULL,
	email varchar(255) NOT NULL,
	firstname varchar(255) NOT NULL,
	lastname varchar(255) NOT NULL,
	password varchar(255) NULL,
	role varchar(255) check (role in ('USER','ADMIN')),
    status varchar(255) check (status in ('ACTIVE','INACTIVE')),
	street_name varchar(255) NULL,
	street_number varchar(255) NULL,
	zip_code varchar(255) NULL,
	CONSTRAINT client_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS ACCOUNT (
	balance numeric(38, 2) NOT NULL,
	client_id int8 NULL,
	id int8 NOT NULL DEFAULT NEXT VALUE FOR account_sequence PRIMARY KEY,
	account_number varchar(255) NOT NULL,
	currency varchar(255) not null check (currency in ('PLN','USD','EUR', 'GBP', 'CHF', 'AUD', 'NOK')),
	type varchar(255) not null check (type in ('CURRENT_ACCOUNT', 'SAVINGS_ACCOUNT')),
	CONSTRAINT account_account_number_key UNIQUE (account_number),
	CONSTRAINT account_pkey PRIMARY KEY (id),
	CONSTRAINT client_id_fk FOREIGN KEY (client_id) REFERENCES client(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS REVOKED_TOKEN (
	id int4 NOT NULL DEFAULT NEXT VALUE FOR token_seq PRIMARY KEY,
	token varchar(255) NULL,
	CONSTRAINT token_pkey PRIMARY KEY (id),
	CONSTRAINT token_token_key UNIQUE (token)
);

CREATE TABLE IF NOT EXISTS TRANSFER_HISTORY (
	amount numeric(38, 2) NOT NULL,
	balance numeric(38, 2) NOT NULL,
	previous_balance numeric(38, 2) NOT NULL,
	client_id int8 NOT NULL,
	created_by int8 NULL,
	created_on timestamp(6) NULL,
	id int8 NOT NULL DEFAULT NEXT VALUE FOR history_generator PRIMARY KEY,
	update_on timestamp(6) NULL,
	updated_by int8 NULL,
	account_number varchar(255) NOT NULL,
	external_account_number varchar(255) NOT NULL,
	local_date_time_pattern varchar(255) NULL,
	title varchar(255) NOT NULL,
	transfer_type varchar(255) not null check (transfer_type in ('INCOME','EXPENSE')),
	CONSTRAINT transfer_history_pkey PRIMARY KEY (id)
);
