-- Chapter 14 윈도우 함수 테스트용 데이터
-- 400번대 ID로 기존 데이터와 충돌 방지

INSERT INTO
    author (id, first_name, last_name)
VALUES (401, 'WinTest', 'AuthorP'),
    (402, 'WinTest', 'AuthorQ')
ON CONFLICT (id) DO NOTHING;

-- AuthorP: 2000, 2010, 2020 (최신 = 2020, RANK 1)
-- AuthorQ: 2005, 2015 (최신 = 2015, RANK 1)
INSERT INTO
    book (
        id,
        title,
        author_id,
        published_year
    )
VALUES (
        401,
        'WinBook Alpha',
        401,
        2000
    ),
    (
        402,
        'WinBook Beta',
        401,
        2010
    ),
    (
        403,
        'WinBook Gamma',
        401,
        2020
    ),
    (
        404,
        'WinBook Delta',
        402,
        2005
    ),
    (
        405,
        'WinBook Epsilon',
        402,
        2015
    )
ON CONFLICT (id) DO NOTHING;