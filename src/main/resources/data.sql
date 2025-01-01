INSERT INTO CLIENT (first_name, last_name, email, birth_date,password,role,street_name,street_number,zip_code,city) VALUES
('Mike','Wazowski','mike.wazowski@gmail.com','1980-01-28','password','USER','MonstersEnc',10,'00-888','Monsters'),
('Randal','Randal','bad.randal@gmail.com','1970-02-28','badpassword','USER','MonstersEnc',11,'01-888','Monsters'),
('Andy','Warchol','andywarchol@gmail.com','1960-03-15','hardpassword','USER','MonstersEnc',50,'02-888','Monsters');

INSERT INTO ACCOUNT (account_number, currency, type, balance, client_id) VALUES
    ('PL54613983300568639363795256','PLN','CURRENT_ACCOUNT',2000, 1),
    ('GB92BARC20038472426896','GBP','CURRENT_ACCOUNT',3000, 2),
    ('DE11500105171841551884','EUR','CURRENT_ACCOUNT',500, 3);

INSERT INTO TOKEN (client_id,expired, revoked, token, token_type) VALUES
        (1,false, false, 'eyJhbGciOiJIUzI1NiJ9.ew0KICAic3ViIjogIjEyMzQ1Njc4OTAiLA0KICAibmFtZSI6ICJBbmlzaCBOYXRoIiwNCiAgImlhdCI6IDE1MTYyMzkwMjINCn0.Zz9sn7N07HMDZ5VQtDWtMrPMBCltEPStAoGe6AU-sC8',
        'BEARER'),
        (2,false, false, 'eyJhbGciOiJIUzI1NiJ9.ew0KICAic3ViIjogIjEyMzQ1Njc4OTAiLA0KICAibmFtZSI6ICJBbmlzaCBOYXRoIiwNCiAgImlhdCI6IDE1MTYyMzkwMjINCn0.944o1fDK9uL9VvIgr7GiXFPzDifCYM8farV5Kdy0Pqc',
        'BEARER'),
        (3,false, false, 'eyJhbGciOiJIUzI1NiJ9.ew0KICAic3ViIjogIjEyMzQ1Njc4OTAiLA0KICAibmFtZSI6ICJBbmlzaCBOYXRoIiwNCiAgImlhdCI6IDE1MTYyMzkwMjINCn0.FUJcwtxr_w4FhlurUbcYNHTu8AP7ErlCUXt9Tjr9af8',
        'BEARER');
