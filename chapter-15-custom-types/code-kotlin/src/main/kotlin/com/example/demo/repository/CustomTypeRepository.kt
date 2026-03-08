package com.example.demo.repository

import com.example.demo.dto.BookWithStatus
import com.example.demo.type.BookStatus
import com.example.jooq.tables.references.BOOK
import org.jooq.Converter
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository

/**
 * 【Chapter 15】 커스텀 데이터 타입과 컨버터 - Kotlin
 */
@Repository
class CustomTypeRepository(
    private val dsl: DSLContext
) {
    private val converter: Converter<String, BookStatus> = Converter.of(
        String::class.java,
        BookStatus::class.java,
        { dbVal -> dbVal?.let { BookStatus.valueOf(it) } },
        { enumVal -> enumVal?.name }
    )

    // status 컬럼 동적 참조 (코드 생성 대상 아님)
    private val statusField = DSL.field("status", String::class.java)

    fun findByStatus(status: BookStatus): List<BookWithStatus> =
        dsl.select(BOOK.ID, BOOK.TITLE, BOOK.PUBLISHED_YEAR, statusField)
            .from(BOOK)
            .where(statusField.eq(converter.to(status)))
            .orderBy(BOOK.TITLE.asc())
            .fetch { r ->
                BookWithStatus(
                    id = r.get(BOOK.ID),
                    title = r.get(BOOK.TITLE),
                    publishedYear = r.get(BOOK.PUBLISHED_YEAR),
                    status = converter.from(r.get(statusField))
                )
            }

    fun findAllWithStatus(): List<BookWithStatus> =
        dsl.select(BOOK.ID, BOOK.TITLE, BOOK.PUBLISHED_YEAR, statusField)
            .from(BOOK)
            .orderBy(BOOK.TITLE.asc())
            .fetch { r ->
                BookWithStatus(
                    id = r.get(BOOK.ID),
                    title = r.get(BOOK.TITLE),
                    publishedYear = r.get(BOOK.PUBLISHED_YEAR),
                    status = converter.from(r.get(statusField))
                )
            }

    fun updateBookStatus(bookId: Int, newStatus: BookStatus): Int =
        dsl.update(BOOK)
            .set(statusField, converter.to(newStatus))
            .where(BOOK.ID.eq(bookId))
            .execute()
}
