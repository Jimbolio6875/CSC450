USE login;

DROP TABLE IF EXISTS `central_login`;
DROP TABLE IF EXISTS `social_media_accounts`;
DROP TABLE IF EXISTS spring_session_attributes;
DROP TABLE IF EXISTS spring_session;

CREATE TABLE `central_login` (
`central_login_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(32) NOT NULL,
  `password` varchar(256) NOT NULL,
  `first_name` varchar(32) NOT NULL,
  `last_name` varchar(32) NOT NULL,
  PRIMARY KEY (`central_login_id`)
);
