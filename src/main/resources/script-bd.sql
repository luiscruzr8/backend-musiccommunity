INSERT INTO roles(name) VALUES('ROLE_USER');

INSERT INTO users(id, email, login, password, phone)
    VALUES(1, 'luis@udc.es', 'Luisin', 'encryptedpassgeneratedbybcrypt', '123456798');
INSERT INTO users(id, email, login, password, phone)
    VALUES(2, 'alonsin@mail.es', 'Alonsin', 'encryptedpassgeneratedbybcrypt', '987654321');
INSERT INTO users(id, email, login, password, phone)
    VALUES(3, 'jorgin@mail.com', 'Jorgin', 'encryptedpassgeneratedbybcrypt', '756438271');
INSERT INTO users(id, email, login, password, phone)
    VALUES(4, 'batman@mail.com', 'Batman', 'encryptedpassgeneratedbybcrypt', '128276849');
INSERT INTO users(id, email, login, password, phone)
    VALUES(5, 'spiderman@mail.com', 'Spiderman', 'encryptedpassgeneratedbybcrypt', '987654321');

INSERT INTO roles_per_user (user_id, role_id) VALUES (1,1);     
INSERT INTO roles_per_user (user_id, role_id) VALUES (2,1);
INSERT INTO roles_per_user (user_id, role_id) VALUES (3,1);     
INSERT INTO roles_per_user (user_id, role_id) VALUES (4,1);
INSERT INTO roles_per_user (user_id, role_id) VALUES (5,1); 

INSERT INTO cities(id, country, name, latitude, longitude) VALUES(1,'España','A Coruña', 43.3712591, -8.4188010);
INSERT INTO cities(id, country, name, latitude, longitude) VALUES(2,'España','Santiago de Compostela', 42.8802410, -8.5473632);
INSERT INTO cities(id, country, name, latitude, longitude) VALUES(3,'España','Barcelona', 41.3818, 2.1685);
INSERT INTO cities(id, country, name, latitude, longitude) VALUES(4,'España','Madrid', 40.4893538, -3.6827461);
INSERT INTO cities(id, country, name, latitude, longitude) VALUES(5,'España','Toledo', 39.8676536, -4.0098788);
INSERT INTO cities(id, country, name, latitude, longitude) VALUES(6,'España','Bilbao', 43.2603479, -2.9334110);
INSERT INTO cities(id, country, name, latitude, longitude) VALUES(7,'España','San Sebastián', 43.2918111, -1.9885133);
INSERT INTO cities(id, country, name, latitude, longitude) VALUES(8,'España','Salamanca', 40.9559681, -5.6802244);
INSERT INTO cities(id, country, name, latitude, longitude) VALUES(9,'España','Valencia', 39.4561165, -0.3545661);
INSERT INTO cities(id, country, name, latitude, longitude) VALUES(10,'España','Granada', 37.1886273, -3.5907775);
INSERT INTO cities(id, country, name, latitude, longitude) VALUES(11,'España','Cádiz', 36.5008762, -6.2684345);
INSERT INTO cities(id, country, name, latitude, longitude) VALUES(12,'España','Tarragona', 41.115, 1.2499);

INSERT INTO tags(id, tag_name) VALUES(1, 'concierto');
INSERT INTO tags(id, tag_name) VALUES(2, 'artesanal');
INSERT INTO tags(id, tag_name) VALUES(3, 'clases');
INSERT INTO tags(id, tag_name) VALUES(4, 'profesor');
INSERT INTO tags(id, tag_name) VALUES(5, 'venta');
INSERT INTO tags(id, tag_name) VALUES(6, 'intrumento');
INSERT INTO tags(id, tag_name) VALUES(7, 'fiesta');
INSERT INTO tags(id, tag_name) VALUES(8, 'concurso');
INSERT INTO tags(id, tag_name) VALUES(9, 'sinfónica');
INSERT INTO tags(id, tag_name) VALUES(10, 'orquesta');
INSERT INTO tags(id, tag_name) VALUES(11, 'luthier');
INSERT INTO tags(id, tag_name) VALUES(12, 'músico');

INSERT INTO tags_per_user (user_id, tag_id) VALUES (1,1);     
INSERT INTO tags_per_user (user_id, tag_id) VALUES (1,7);
INSERT INTO tags_per_user (user_id, tag_id) VALUES (1,9);     
INSERT INTO tags_per_user (user_id, tag_id) VALUES (1,10);
INSERT INTO tags_per_user (user_id, tag_id) VALUES (2,3);     
INSERT INTO tags_per_user (user_id, tag_id) VALUES (2,4);
INSERT INTO tags_per_user (user_id, tag_id) VALUES (2,11);    
INSERT INTO tags_per_user (user_id, tag_id) VALUES (3,2);
INSERT INTO tags_per_user (user_id, tag_id) VALUES (4,5);     
INSERT INTO tags_per_user (user_id, tag_id) VALUES (4,6);
INSERT INTO tags_per_user (user_id, tag_id) VALUES (5,1);     
INSERT INTO tags_per_user (user_id, tag_id) VALUES (5,8);
