CREATE DATABASE IF NOT EXISTS esports_db;
USE esports_db;

CREATE TABLE IF NOT EXISTS organizers (
    org_id   VARCHAR(30) PRIMARY KEY,
    password VARCHAR(30) NOT NULL
);

CREATE TABLE IF NOT EXISTS players (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    gmail         VARCHAR(100) NOT NULL,
    mobile        VARCHAR(15)  NOT NULL,
    team_name     VARCHAR(100) NOT NULL,
    points        INT DEFAULT 0,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO organizers VALUES ('admin', '1234');
INSERT INTO organizers VALUES ('rohit','$1234');