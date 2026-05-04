CREATE DATABASE paymybuddy;
USE paymybuddy;
CREATE TABLE users (
id INTEGER NOT NULL AUTO_INCREMENT,
email VARCHAR(100) NOT NULL UNIQUE,
username VARCHAR(100) NOT NULL,
password VARCHAR(255) NOT NULL,
balance DOUBLE NOT NULL DEFAULT 0;
PRIMARY KEY (id)
);
CREATE TABLE transaction (
id INTEGER NOT NULL AUTO_INCREMENT,
sender INTEGER NOT NULL,
receiver INTEGER NOT NULL ,
description VARCHAR(100),
amount DOUBLE NOT NULL,
date_transaction DATETIME NOT NULL,
PRIMARY KEY (id),
FOREIGN KEY (sender) REFERENCES users(id),
FOREIGN KEY (receiver) REFERENCES users(id),
CHECK (amount > 0),
CHECK (sender <> receiver)
);
CREATE TABLE assoc_user (
id_user1 INTEGER NOT NULL,
id_user2 INTEGER NOT NULL,
PRIMARY KEY (id_user1, id_user2),
FOREIGN KEY (id_user1) REFERENCES users(id),
FOREIGN KEY (id_user2) REFERENCES users(id)
);
INSERT INTO users (email, username, password)
VALUES ( 'shade@gmail.com' , 'Shade' , '$2a$10$hash_fictif_shade');
