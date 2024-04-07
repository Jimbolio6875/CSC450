USE login;

DROP TABLE IF EXISTS mastodon;

CREATE TABLE mastodon
(
    id SERIAL,
    post_id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    content VARCHAR(255) NOT NULL,
    post_url VARCHAR(255) NOT NULL,
    favourite_count INTEGER NOT NULL
);

ALTER TABLE mastodon
    ADD PRIMARY KEY (id);
#     ADD CONSTRAINT fk_central_login_id FOREIGN KEY (central_login_id) REFERENCES central_login (central_login_id);

SELECT * FROM mastodon;