use login;
DROP TABLE IF EXISTS mastodon;
DROP TABLE IF EXISTS tumblr;
DROP TABLE IF EXISTS twitter;
DROP TABLE IF EXISTS reddit_posts;
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

CREATE TABLE mastodon
(
    id               SERIAL PRIMARY KEY,
    post_id          VARCHAR(255),
    user_id          VARCHAR(255),
    content          VARCHAR(512),
    post_url         VARCHAR(255),
    favourite_count  INTEGER,
    access_token     VARCHAR(1000) NOT NULL,
    central_login_id INT,
    FOREIGN KEY (central_login_id) REFERENCES central_login (central_login_id)
);

CREATE TABLE tumblr
(
    id               SERIAL PRIMARY KEY,
    post_id          VARCHAR(255),
    access_token     VARCHAR(255) NOT NULL,
    token_secret     VARCHAR(255) NOT NULL,
    blog_identifier  VARCHAR(255) NOT NULL,
    content          MEDIUMTEXT,
    post_url         VARCHAR(255),
    note_count       INTEGER,
    date             TIMESTAMP,
    central_login_id INT,
    FOREIGN KEY (central_login_id) REFERENCES central_login (central_login_id)
);

CREATE TABLE twitter
(
    id                  SERIAL PRIMARY KEY,
    tweet_text          TEXT,
    creation_date       TIMESTAMP,
    access_token        VARCHAR(320) NOT NULL,
    access_token_secret VARCHAR(320) NOT NULL,
    central_login_id    INT,
    FOREIGN KEY (central_login_id) REFERENCES central_login (central_login_id)
);


CREATE TABLE reddit_posts
(
    id               INT AUTO_INCREMENT PRIMARY KEY,
    post_id          VARCHAR(255),
    subreddit        VARCHAR(255),
    title            TEXT,
    content          TEXT,
    author           VARCHAR(255),
    up_votes         INTEGER   DEFAULT 0,
    down_votes       INTEGER   DEFAULT 0,
    score            INT       DEFAULT 0,
    num_comments     INT       DEFAULT 0,
    url              TEXT,
    creation_date    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    access_token     VARCHAR(1000) NOT NULL,
    central_login_id INT,
    FOREIGN KEY (central_login_id) REFERENCES central_login (central_login_id)
);