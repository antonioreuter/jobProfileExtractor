DROP TABLE GithubJobProfile IF EXISTS;

CREATE TABLE GithubJobProfile (
    id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    login VARCHAR(50) NOT NULL,
    url VARCHAR(255) NOT NULL,
    avatar_url VARCHAR(255),
    repos_url VARCHAR(255),
    type VARCHAR(30) NOT NULL,
    score FLOAT NOT NULL
 )
