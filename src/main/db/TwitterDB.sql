DROP TABLE IF EXISTS twitter;


CREATE TABLE twitter
(
    id            SERIAL PRIMARY KEY,
    tweet_id      BIGINT NOT NULL,
    tweet_text    TEXT   NOT NULL,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);