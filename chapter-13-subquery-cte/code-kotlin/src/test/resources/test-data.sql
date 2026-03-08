-- Chapter 13 서브쿼리/CTE 테스트용 데이터 (Kotlin)
INSERT INTO
    author (id, first_name, last_name)
VALUES (301, 'CteTest', 'AuthorX'),
    (302, 'CteTest', 'AuthorY')
ON CONFLICT (id) DO NOTHING;

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