DROP TABLE IF EXISTS reddit_posts;

CREATE TABLE reddit_posts
(
    id            INT AUTO_INCREMENT PRIMARY KEY,
    post_id       VARCHAR(255),
    subreddit     VARCHAR(255),
    title         TEXT,
    content       TEXT,
    author        VARCHAR(255),
    up_votes      INTEGER   DEFAULT 0,
    down_votes    INTEGER   DEFAULT 0,
    score         INT       DEFAULT 0,
    num_comments  INT       DEFAULT 0,
    url           TEXT,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    access_token  VARCHAR(1000) NOT NULL
);

