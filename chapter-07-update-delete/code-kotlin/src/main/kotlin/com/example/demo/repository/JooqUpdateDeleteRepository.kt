package com.example.demo.repository

import com.example.jooq.tables.records.AuthorRecord
import com.example.jooq.tables.references.AUTHOR
import com.example.jooq.tables.references.BOOK
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class JooqUpdateDeleteRepository(
    private val dsl: DSLContext
) {

    /**
     * 【DSL 스타일 UPDATE】 책 제목을 수정하고 영향받은 행 수를 반환합니다.
     * SQL: UPDATE book SET title = ? WHERE id = ?
     */
    fun updateBookTitle(bookId: Int, newTitle: String): Int {
        return dsl.update(BOOK)
            .set(BOOK.TITLE, newTitle)
            .where(BOOK.ID.eq(bookId))
            .execute()
    }

    /**
     * 【UpdatableRecord 스타일 UPDATE】 작가를 조회 후 이름을 수정하고 저장된 레코드를 반환합니다.
     * Kotlin의 let {} 블록으로 null-safe하게 처리합니다.
     */
    fun updateAuthorWithRecord(authorId: Int, newFirstName: String): AuthorRecord {
        return dsl.fetchOne(AUTHOR, AUTHOR.ID.eq(authorId))?.also {
            it.firstName = newFirstName
            it.store() // PK 존재 → 자동 UPDATE
        } ?: throw IllegalArgumentException("Author not found: $authorId")
    }

    /**
     * 【Hard Delete (물리 삭제)】 책을 완전히 삭제하고 영향받은 행 수를 반환합니다.
     * SQL: DELETE FROM book WHERE id = ?
     */
    fun deleteBookById(bookId: Int): Int {
        return dsl.deleteFrom(BOOK)
            .where(BOOK.ID.eq(bookId))
            .execute()
    }

    /**
     * 【Soft Delete (논리 삭제)】 deleted_at 컬럼에 현재 시각을 기록합니다.
     * SQL: UPDATE book SET deleted_at = NOW() WHERE id = ? AND deleted_at IS NULL
     */
    fun softDeleteBook(bookId: Int): Int {
        return dsl.update(BOOK)
            .set(BOOK.DELETED_AT, LocalDateTime.now())
            .where(BOOK.ID.eq(bookId))
            .and(BOOK.DELETED_AT.isNull) // 이미 삭제된 것 방지 (멱등성)
            .execute()
    }
}
