INSERT INTO CLIENT (firstname, lastname, email, birth_date, password, role, status, street_name, street_number,
                    zip_code, city, created_on, created_by, update_on, updated_by)
VALUES ('Mike', 'Wazowski', 'mike.wazowski@gmail.com', '1980-01-28', '{noop}password', 'ADMIN', 'ACTIVE', 'MonstersEnc',
        10, '00-888', 'Monsters', CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 1),
       ('Randal', 'Randal', 'bad.randal@gmail.com', '1970-02-28', '{noop}badpassword', 'USER', 'ACTIVE', 'MonstersEnc',
        11, '01-888', 'Monsters', CURRENT_TIMESTAMP, 2, CURRENT_TIMESTAMP, 2),
       ('Andy', 'Warchol', 'andywarchol@gmail.com', '1960-03-15', '{noop}hardpassword', 'USER', 'ACTIVE', 'MonstersEnc',
        50, '02-888', 'Monsters', CURRENT_TIMESTAMP, 3, CURRENT_TIMESTAMP, 3);

INSERT INTO ACCOUNT (account_number, currency, type, balance, client_id)
VALUES ('PL54613983300568639363795256', 'PLN', 'CURRENT_ACCOUNT', 2000, 1),
       ('GB92BARC20038472426896', 'GBP', 'CURRENT_ACCOUNT', 3000, 2),
       ('DE11500105171841551884', 'EUR', 'CURRENT_ACCOUNT', 500, 3);

INSERT INTO REVOKED_TOKEN (token)
VALUES ('eyJhbGciOiJIUzI1NiJ9.ew0KICAic3ViIjogIjEyMzQ1Njc4OTAiLA0KICAibmFtZSI6ICJBbmlzaCBOYXRoIiwNCiAgImlhdCI6IDE1MT' ||
        'YyMzkwMjINCn0.Zz9sn7N07HMDZ5VQtDWtMrPMBCltEPStAoGe6AU-sC8'),
       ('eyJhbGciOiJIUzI1NiJ9.ew0KICAic3ViIjogIjEyMzQ1Njc4OTAiLA0KICAibmFtZSI6ICJBbmlzaCBOYXRoIiwNCiAgImlhdCI6IDE1MT' ||
        'YyMzkwMjINCn0.944o1fDK9uL9VvIgr7GiXFPzDifCYM8farV5Kdy0Pqc'),
       ('eyJhbGciOiJIUzI1NiJ9.ew0KICAic3ViIjogIjEyMzQ1Njc4OTAiLA0KICAibmFtZSI6ICJBbmlzaCBOYXRoIiwNCiAgImlhdCI6IDE1MT' ||
        'YyMzkwMjINCn0.FUJcwtxr_w4FhlurUbcYNHTu8AP7ErlCUXt9Tjr9af8');

INSERT INTO TRANSFER_HISTORY (amount, balance, previous_balance, client_id, account_number, external_account_number,
                              title, transfer_type, created_on)
VALUES (200, 800, 1000, 1, 'PL21363593769265669736300815', 'PL27722968758620190053098782', 'Money for nothing',
        'EXPENSE', '2024-12-16 10:20:25'),
       (200, 500, 300, 1, 'PL27722968758620190053098782', 'PL21363593769265669736300815', 'Money for nothing',
        'INCOME', '2024-12-16 10:20:25'),
       (100, 700, 800, 1, 'PL21363593769265669736300815', 'PL27722968758620190053098782', 'Money for food',
        'EXPENSE', '2025-01-10 13:45:59'),
       (100, 600, 500, 1, 'PL27722968758620190053098782', 'PL21363593769265669736300815', 'Money for food',
        'INCOME', '2025-01-10 13:45:59');
