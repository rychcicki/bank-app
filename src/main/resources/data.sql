INSERT INTO CLIENT (id,first_name, last_name, birth_date,email,password,role,street_name,street_number,zip_code,city) VALUES
(1,'Mike','Wazowski','1980-01-28','mike.wazowski@gmail.com','password','USER','MonstersEnc',10,'00-888','Monsters'),
(2,'Randal','Randal','1970-02-28','bad.randal@gmail.com','badpassword','USER','MonstersEnc',11,'01-888','Monsters'),
(3,'Andy','Warchol','1960-03-15','andywarchol@gmail.com','hardpassword','USER','MonstersEnc',50,'02-888','Monsters');
--
--INSERT INTO ACCOUNT (id,account_number, currency, type, balance) VALUES
--    (1,'PL54613983300568639363795256','PLN','CURRENT_ACCOUNT',2000),
--    (2,'GB92BARC20038472426896','GBP','CURRENT_ACCOUNT',3000),
--    (3,'DE11500105171841551884','EUR','CURRENT_ACCOUNT',500);
--
--INSERT INTO TOKEN (id, client_id,expired, revoked, token, token_type) VALUES
--        (1,1,false, false, 'eyJhbGciOiJIUzI1NiJ9.ew0KICAic3ViIjogIjEyMzQ1Njc4OTAiLA0KICAibmFtZSI6ICJBbmlzaCBOYXRoIiwNCiAgImlhdCI6IDE1MTYyMzkwMjINCn0.Zz9sn7N07HMDZ5VQtDWtMrPMBCltEPStAoGe6AU-sC8',
--        'BEARER'),
--        (2,2false, false, 'eyJhbGciOiJIUzI1NiJ9.ew0KICAic3ViIjogIjEyMzQ1Njc4OTAiLA0KICAibmFtZSI6ICJBbmlzaCBOYXRoIiwNCiAgImlhdCI6IDE1MTYyMzkwMjINCn0.944o1fDK9uL9VvIgr7GiXFPzDifCYM8farV5Kdy0Pqc',
--        'BEARER'),
--        (3,3false, false, 'eyJhbGciOiJIUzI1NiJ9.ew0KICAic3ViIjogIjEyMzQ1Njc4OTAiLA0KICAibmFtZSI6ICJBbmlzaCBOYXRoIiwNCiAgImlhdCI6IDE1MTYyMzkwMjINCn0.FUJcwtxr_w4FhlurUbcYNHTu8AP7ErlCUXt9Tjr9af8',
--        'BEARER');
