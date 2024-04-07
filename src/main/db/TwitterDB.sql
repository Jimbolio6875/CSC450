DROP TABLE IF EXISTS twitter;


CREATE TABLE twitter
(
    id                  SERIAL PRIMARY KEY,
    tweet_text          TEXT,
    creation_date       TIMESTAMP,
    access_token        VARCHAR(320) NOT NULL,
    access_token_secret VARCHAR(320) NOT NULL
);