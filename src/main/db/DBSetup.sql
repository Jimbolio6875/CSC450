CREATE SCHEMA `login` DEFAULT CHARACTER SET utf8mb4;

CREATE USER 'centrallogin'@'localhost' IDENTIFIED BY 'centrallogin';
GRANT ALL PRIVILEGES ON login . * TO 'centrallogin'@'localhost';
FLUSH PRIVILEGES;

CREATE USER 'centrallogin'@'127.0.0.1' IDENTIFIED BY 'centrallogin';
GRANT ALL PRIVILEGES ON login . * TO 'centrallogin'@'127.0.0.1';
FLUSH PRIVILEGES;

CREATE USER 'centrallogin'@'%' IDENTIFIED BY 'centrallogin';
GRANT ALL PRIVILEGES ON login . * TO 'centrallogin';
FLUSH PRIVILEGES;