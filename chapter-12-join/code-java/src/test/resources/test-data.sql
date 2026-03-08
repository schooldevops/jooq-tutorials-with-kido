-- Chapter 12 JOIN 테스트용 데이터
-- FK 제약(fk_book_author) 임시 비활성화 후 저자 없는 책 삽입 → LEFT JOIN 테스트용

-- 저자 추가
INSERT INTO
    author (id, first_name, last_name)
VALUES (201, 'JoinTest', 'AuthorA'),
    (202, 'JoinTest', 'AuthorB')
ON CONFLICT (id) DO NOTHING;

-- FK 제약 임시 비활성화
ALTER TABLE book DISABLE TRIGGER ALL;

-- 책 추가
-- JoinBook Alpha/Beta: author 201 (있음)
-- JoinBook Gamma: author 202 (있음)
-- JoinBook Delta: author 299 (없음) → LEFT JOIN 시 firstName=null
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

-- FK 제약 재활성화
ALTER TABLE book ENABLE TRIGGER ALL;