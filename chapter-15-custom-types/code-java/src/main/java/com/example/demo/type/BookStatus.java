package com.example.demo.type;

/**
 * 책의 출판 상태를 나타내는 Enum
 * DB에는 VARCHAR로 저장됩니다. (DRAFT / PUBLISHED / ARCHIVED)
 */
public enum BookStatus {
    DRAFT,
    PUBLISHED,
    ARCHIVED
}
