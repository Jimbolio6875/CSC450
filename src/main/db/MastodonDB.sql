USE login;

DROP TABLE IF EXISTS mastodon;

CREATE TABLE mastodon
(
    post_id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    content VARCHAR(255) NOT NULL,
    post_url VARCHAR(255) NOT NULL,
    favourite_count INTEGER NOT NULL
);

SELECT * FROM mastodon;