package com.example.demo.repository

import com.example.demo.dto.BookWithAuthor
import com.example.jooq.tables.references.AUTHOR
import com.example.jooq.tables.references.BOOK
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

/**
 * 【Chapter 12】 다중 테이블 조인 (JOIN) - Kotlin
 */
@Repository
class JoinRepository(
    private val dsl: DSLContext
) {

    /**
     * INNER JOIN: 저자가 매핑된 책만 반환
     */
    fun findBooksWithAuthor(): List<BookWithAuthor> =
        dsl.select(
                BOOK.ID,
                BOOK.TITLE,
                BOOK.PUBLISHED_YEAR,
                AUTHOR.FIRST_NAME,
                AUTHOR.LAST_NAME
            )
            .from(BOOK)
            .join(AUTHOR).on(BOOK.AUTHOR_ID.eq(AUTHOR.ID))
            .orderBy(BOOK.TITLE.asc())
            .fetchInto(BookWithAuthor::class.java)

    /**
     * LEFT JOIN: 저자 없는 책도 포함, firstName/lastName은 null 가능
     */
    fun findAllBooksWithAuthor(): List<BookWithAuthor> =
        dsl.select(
                BOOK.ID,
                BOOK.TITLE,
                BOOK.PUBLISHED_YEAR,
                AUTHOR.FIRST_NAME,
                AUTHOR.LAST_NAME
            )
            .from(BOOK)
            .leftJoin(AUTHOR).on(BOOK.AUTHOR_ID.eq(AUTHOR.ID))
            .orderBy(BOOK.TITLE.asc())
            .fetchInto(BookWithAuthor::class.java)

    /**
     * INNER JOIN + WHERE: 특정 연도 이후 책 + 저자 정보
     */
    fun findBooksAfterYearWithAuthor(year: Int): List<BookWithAuthor> =
        dsl.select(
                BOOK.ID,
                BOOK.TITLE,
                BOOK.PUBLISHED_YEAR,
                AUTHOR.FIRST_NAME,
                AUTHOR.LAST_NAME
            )
            .from(BOOK)
            .join(AUTHOR).on(BOOK.AUTHOR_ID.eq(AUTHOR.ID))
            .where(BOOK.PUBLISHED_YEAR.ge(year))
            .orderBy(BOOK.PUBLISHED_YEAR.asc())
            .fetchInto(BookWithAuthor::class.java)
}
