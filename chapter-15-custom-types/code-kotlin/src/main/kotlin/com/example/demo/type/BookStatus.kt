package com.example.demo.type

/**
 * 책의 출판 상태를 나타내는 Kotlin Enum
 * DB에는 VARCHAR로 저장됩니다.
 */
enum class BookStatus {
    DRAFT, PUBLISHED, ARCHIVED
}
