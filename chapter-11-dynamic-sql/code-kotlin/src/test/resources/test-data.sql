-- Chapter 11 동적 SQL 테스트용 데이터 추가
-- 이전 챕터들의 데이터와 충돌하지 않도록 ID를 100번대부터 사용합니다.

INSERT INTO
    author (id, first_name, last_name)
VALUES (
        101,
        'William',
        'DynamicShakespeare'
    ),
    (102, 'Jane', 'DynamicAusten')
ON CONFLICT (id) DO NOTHING;

-- 책 추가
INSERT INTO
    book (
        id,
        title,
        author_id,
        published_year
    )
VALUES (
        101,
        'DynamicHamlet',
        101,
        1600
    ),
    (
        102,
        'DynamicRomeo and Juliet',
        101,
        1597
    ),
    (
        103,
        'DynamicPride and Prejudice',
        102,
        1813
    ),
    (
        104,
        'DynamicSense and Sensibility',
        102,
        1811
    ),
    (
        105,
        'The Dynamic Coder',
        101,
        2011
    )
ON CONFLICT (id) DO NOTHING;