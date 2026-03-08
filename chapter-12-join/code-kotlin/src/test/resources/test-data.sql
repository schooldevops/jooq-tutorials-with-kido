-- Chapter 12 JOIN 테스트용 데이터 (Kotlin)
INSERT INTO
    author (id, first_name, last_name)
VALUES (201, 'JoinTest', 'AuthorA'),
    (202, 'JoinTest', 'AuthorB')
ON CONFLICT (id) DO NOTHING;

ALTER TABLE book DISABLE TRIGGER ALL;

INSERT INTO
    book (
        id,
        title,
        author_id,
        published_year
    )
VALUES (
        201,
        'JoinBook Alpha',
        201,
        2000
    ),
    (
        202,
        'JoinBook Beta',
        201,
        2010
    ),
    (
        203,
        'JoinBook Gamma',
        202,
        2020
    ),
    (
        204,
        'JoinBook Delta',
        299,
        2015
    )
ON CONFLICT (id) DO NOTHING;

ALTER TABLE book ENABLE TRIGGER ALL;