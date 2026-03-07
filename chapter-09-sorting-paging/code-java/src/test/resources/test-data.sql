-- 페이징 테스트를 위한 추가 Book 데이터
-- 실제 author ID는 init-script seeded (id=1: Shakespeare, id=2: Austen)
INSERT INTO
    book (
        title,
        author_id,
        published_year
    )
VALUES ('Anna Karenina', 1, 1877);

INSERT INTO
    book (
        title,
        author_id,
        published_year
    )
VALUES ('Emma', 2, 1815);

INSERT INTO
    book (
        title,
        author_id,
        published_year
    )
VALUES (
        'Sense and Sensibility',
        2,
        1811
    );

INSERT INTO
    book (
        title,
        author_id,
        published_year
    )
VALUES ('Othello', 1, 1604);

INSERT INTO
    book (
        title,
        author_id,
        published_year
    )
VALUES ('Macbeth', 1, 1606);