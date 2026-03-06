CREATE TABLE author (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(50),
    last_name VARCHAR(50) NOT NULL
);

CREATE TABLE book (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author_id INT NOT NULL,
    published_year INT,
    CONSTRAINT fk_book_author FOREIGN KEY (author_id) REFERENCES author (id)
);

INSERT INTO
    author (first_name, last_name)
VALUES ('William', 'Shakespeare');

INSERT INTO author (first_name, last_name) VALUES ('Jane', 'Austen');

INSERT INTO
    book (
        title,
        author_id,
        published_year
    )
VALUES ('Hamlet', 1, 1603);

INSERT INTO
    book (
        title,
        author_id,
        published_year
    )
VALUES (
        'Pride and Prejudice',
        2,
        1813
    );