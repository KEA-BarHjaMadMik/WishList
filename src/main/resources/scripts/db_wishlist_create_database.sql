DROP DATABASE IF EXISTS wishlist_db;
CREATE DATABASE wishlist_db;
USE wishlist_db;

CREATE TABLE user_account (
    username VARCHAR(15) NOT NULL,
    password VARCHAR(30) NOT NULL,
    email VARCHAR(254) NOT NULL,
    PRIMARY KEY (username),
    UNIQUE(email)
);

CREATE TABLE wish_list (
    id INT NOT NULL AUTO_INCREMENT,
    username VARCHAR(15) NOT NULL,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(800) NULL,
    eventdate DATE NULL,
    private BOOLEAN NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT fk_wish_list_user_account FOREIGN KEY (username)
        REFERENCES user_account(username)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE wish_item (
    id INT NOT NULL AUTO_INCREMENT,
    wish_list_id INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    favourite BOOLEAN NOT NULL DEFAULT 0,
    description VARCHAR(800) NULL,
    price DECIMAL(10,2) NULL,
    quantity INT NOT NULL DEFAULT 1,
    link VARCHAR(400) NULL,
    reserved BOOLEAN NOT NULL DEFAULT 0,
    reserved_by VARCHAR(15) NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_wish_list_id FOREIGN KEY (wish_list_id)
        REFERENCES wish_list (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_wish_item_reserved_by FOREIGN KEY (reserved_by)
        REFERENCES user_account (username)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);