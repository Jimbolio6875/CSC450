DROP TABLE IF EXISTS reddit_posts;

CREATE TABLE reddit_posts
(
    id            INT AUTO_INCREMENT PRIMARY KEY,
    post_id       VARCHAR(255) NOT NULL,
    subreddit     VARCHAR(255) NOT NULL,
    title         TEXT         NOT NULL,
    content       TEXT,
    author        VARCHAR(255) NOT NULL,
    up_votes      INTEGER   DEFAULT 0,
    down_votes    INTEGER   DEFAULT 0,
    score         INT       DEFAULT 0,
    num_comments  INT       DEFAULT 0,
    url           TEXT         NOT NULL,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
