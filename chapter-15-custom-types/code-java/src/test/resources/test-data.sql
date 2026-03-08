-- Chapter 15 커스텀 타입 테스트용 데이터
-- book 테이블에 status 컬럼 추가 (테스트 전용)
ALTER TABLE book
ADD COLUMN IF NOT EXISTS status VARCHAR(20) DEFAULT 'PUBLISHED';

-- 500번대 ID로 기존 데이터와 충돌 방지
INSERT INTO
    author (id, first_name, last_name)
VALUES (501, 'TypeTest', 'AuthorM')
ON CONFLICT (id) DO NOTHING;

INSERT INTO
    book (
        id,
        title,
        author_id,
        published_year,
        status
    )
VALUES (
        501,
        'TypeBook Published',
        501,
        2020,
        'PUBLISHED'
    ),
    (
        502,
        'TypeBook Draft',
        501,
        2021,
        'DRAFT'
    ),
    (
        503,
        'TypeBook Archived',
        501,
        2019,
        'ARCHIVED'
    ),
    (
        504,
        'TypeBook Published2',
        501,
        2022,
        'PUBLISHED'
    )
ON CONFLICT (id) DO NOTHING;