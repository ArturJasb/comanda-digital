-- Provisionamento do banco para rodar LOCAL usando o MariaDB ja instalado na maquina.
-- (Alternativa ao docker-compose, que usa MySQL 8 com root/root.)
-- Rode como root do banco:  sudo mariadb < docs/db-setup-mariadb.sql

CREATE DATABASE IF NOT EXISTS comanda_digital
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'comanda'@'localhost' IDENTIFIED BY 'comanda';
CREATE USER IF NOT EXISTS 'comanda'@'127.0.0.1' IDENTIFIED BY 'comanda';

GRANT ALL PRIVILEGES ON comanda_digital.* TO 'comanda'@'localhost';
GRANT ALL PRIVILEGES ON comanda_digital.* TO 'comanda'@'127.0.0.1';
FLUSH PRIVILEGES;
