DROP TABLE IF EXISTS tumblr;
CREATE TABLE tumblr
(
    id              SERIAL PRIMARY KEY,
    post_id         VARCHAR(255),
    access_token    VARCHAR(255) NOT NULL,
    token_secret    VARCHAR(255) NOT NULL,
    blog_identifier VARCHAR(255) NOT NULL,
    content         VARCHAR(255),
    post_url        VARCHAR(255),
    note_count      INTEGER,
    date            TIMESTAMP
);