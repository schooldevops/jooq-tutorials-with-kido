-- Chapter 13 서브쿼리/CTE 테스트용 데이터
-- 200번대 ID로 기존 데이터와 충돌 방지

INSERT INTO
    author (id, first_name, last_name)
VALUES (301, 'CteTest', 'AuthorX'),
    (302, 'CteTest', 'AuthorY')
ON CONFLICT (id) DO NOTHING;

-- AuthorX: 2005, 2015 (최신=2015)
-- AuthorY: 2018, 2022 (최신=2022)
INSERT INTO
    book (
        id,
        title,
        author_id,
        published_year
    )
VALUES (301, 'CteBook A', 301, 2005),
    (302, 'CteBook B', 301, 2015),
    (303, 'CteBook C', 302, 2018),
    (304, 'CteBook D', 302, 2022)
ON CONFLICT (id) DO NOTHING;