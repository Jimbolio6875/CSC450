USE login;

DROP TABLE IF EXISTS `central_login`;
DROP TABLE IF EXISTS `social_media_accounts`;
DROP TABLE IF EXISTS spring_session_attributes;
DROP TABLE IF EXISTS spring_session;

CREATE TABLE `central_login` (
`central_login_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(32) NOT NULL,
  `password` varchar(32) NOT NULL,
  `first_name` varchar(32) NOT NULL,
  `last_name` varchar(32) NOT NULL,
  -- Going to have to insert references to external login/token tables. For now, I just want this to work
  PRIMARY KEY (`central_login_id`)
);

-- SELECT * FROM central_login;
--
-- CREATE TABLE social_media_accounts
-- (
--     account_id    INT AUTO_INCREMENT PRIMARY KEY,
--     access_token  VARCHAR(1024),
--     platform_name VARCHAR(255) NOT NULL,
--     user_id       TEXT         NOT NULL
-- );