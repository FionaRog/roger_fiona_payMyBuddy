CREATE DATABASE paymybuddy;
USE paymybuddy;
CREATE TABLE user (
id INTEGER NOT NULL AUTO_INCREMENT,
email VARCHAR(100) NOT NULL UNIQUE,
username VARCHAR(100) NOT NULL,
password VARCHAR(255) NOT NULL,
PRIMARY KEY (id)
);
CREATE TABLE transaction (
id INTEGER NOT NULL AUTO_INCREMENT,
sender INTEGER NOT NULL,
receiver INTEGER NOT NULL ,
description VARCHAR(100),
amount DOUBLE NOT NULL,
date_transaction DATE NOT NULL,
PRIMARY KEY (id),
FOREIGN KEY (sender) REFERENCES user(id),
FOREIGN KEY (receiver) REFERENCES user(id),
CHECK (amount > 0),
CHECK (sender <> receiver)
);
CREATE TABLE assoc_user (
id_user1 INTEGER NOT NULL,
id_user2 INTEGER NOT NULL,
PRIMARY KEY (id_user1, id_user2),
FOREIGN KEY (id_user1) REFERENCES user(id),
FOREIGN KEY (id_user2) REFERENCES user(id)
);
INSERT INTO user (email, username, password)
VALUES ( 'shade@gmail.com' , 'Shade' , '$2a$10$hash_fictif_shade');
INSERT INTO user (email, username, password)
VALUES ( 'Vorn@gmail.com' , 'Vorn' , '$2a$10$hash_fictif_vorn');
INSERT INTO user (email, username, password)
VALUES ( 'spiky@gmail.com' , 'Spiky' , '$2a$10$hash_fictif_spiky');
INSERT INTO transaction (sender, receiver, description, amount, date_transaction)
VALUES (1, 2, 'Remboursement tente', 49.90, '2026-03-30'); 
INSERT INTO transaction (sender, receiver, description, amount, date_transaction)
VALUES (2, 3, 'Paiement dette', 100.00, '2026-03-29');
INSERT INTO transaction (sender, receiver, description, amount, date_transaction)
VALUES (3, 1, 'Partage loyer', 250.00, '2026-03-01');
INSERT INTO assoc_user (id_user1, id_user2)
VALUES (1, 2);
INSERT INTO assoc_user (id_user1, id_user2)
VALUES (1,3);
INSERT INTO assoc_user (id_user1, id_user2)
VALUES (2, 3);
SELECT * FROM user;
SELECT * FROM transaction;
SELECT * FROM assoc_user;