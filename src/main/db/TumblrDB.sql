DROP TABLE IF EXISTS tumblr;
CREATE TABLE tumblr
(
    post_id VARCHAR(255) NOT NULL,
    blog_identifier VARCHAR(255) NOT NULL,
    content VARCHAR(255) NOT NULL,
    post_url VARCHAR(255) NOT NULL,
    note_count INTEGER NOT NULL,
    date TIMESTAMP NOT NULL
);

SELECT * FROM tumblr;