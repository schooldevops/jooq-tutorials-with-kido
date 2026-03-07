-- Chapter 07: Update & Delete 실습을 위한 스키마 확장
-- book 테이블에 soft delete용 deleted_at 컬럼 추가
ALTER TABLE book ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;