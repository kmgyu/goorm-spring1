CREATE TABLE posts (
                       seq BIGINT NOT NULL AUTO_INCREMENT,
                       title VARCHAR(200) NOT NULL,
                       content TEXT NOT NULL,
                       author VARCHAR(50) NOT NULL,
                       created_at DATETIME(6) NOT NULL,
                       PRIMARY KEY (seq)
);
