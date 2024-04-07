USE login;

DROP TABLE IF EXISTS mastodon;

CREATE TABLE mastodon
(
    id              SERIAL PRIMARY KEY,
    post_id         VARCHAR(255),
    user_id         VARCHAR(255),
    content         VARCHAR(255),
    post_url        VARCHAR(255),
    favourite_count INTEGER,
    access_token    VARCHAR(1000) NOT NULL
);

SELECT *
FROM mastodon;