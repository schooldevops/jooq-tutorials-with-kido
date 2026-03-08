package com.example.demo.repository

import com.example.demo.dto.AuthorBookCount
import com.example.demo.dto.RecentBook
import com.example.jooq.tables.pojos.Book
import com.example.jooq.tables.references.AUTHOR
import com.example.jooq.tables.references.BOOK
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository

/**
 * 【Chapter 13】 서브쿼리와 CTE - Kotlin
 */
@Repository
class SubqueryCteRepository(
    private val dsl: DSLContext
) {

    /**
     * Scalar Subquery: 전체 평균 published_year보다 이후 책 조회
     */
    fun findBooksAboveAvgYear(): List<Book> {
        val avgYearSubquery = DSL.select(DSL.avg(BOOK.PUBLISHED_YEAR)).from(BOOK)

        return dsl.selectFrom(BOOK)
            .where(BOOK.PUBLISHED_YEAR.gt(
                DSL.coerce(avgYearSubquery.asField<java.math.BigDecimal>(), Int::class.java)
            ))
            .orderBy(BOOK.PUBLISHED_YEAR.desc())
            .fetchInto(Book::class.java)
    }

    /**
     * CTE: 저자별 책 수 집계
     */
    fun findAuthorsWithBookCount(): List<AuthorBookCount> {
        val bookCountCte = DSL.name("book_count")
            .fields("author_id", "cnt")
            .`as`(
                DSL.select(BOOK.AUTHOR_ID, DSL.count().`as`("cnt"))
                    .from(BOOK)
                    .groupBy(BOOK.AUTHOR_ID)
            )

        return dsl.with(bookCountCte)
            .select(
                AUTHOR.ID,
                AUTHOR.FIRST_NAME,
                AUTHOR.LAST_NAME,
                bookCountCte.field("cnt")
            )
            .from(bookCountCte)
            .join(AUTHOR).on(AUTHOR.ID.eq(bookCountCte.field("author_id", Int::class.java)))
            .orderBy(bookCountCte.field("cnt", Int::class.java)!!.desc())
            .fetchInto(AuthorBookCount::class.java)
    }

    /**
     * CTE + JOIN: 저자별 가장 최신 책 조회
     */
    fun findRecentBooksPerAuthor(): List<RecentBook> {
        val maxYearCte = DSL.name("max_year")
            .fields("author_id", "max_y")
            .`as`(
                DSL.select(BOOK.AUTHOR_ID, DSL.max(BOOK.PUBLISHED_YEAR).`as`("max_y"))
                    .from(BOOK)
                    .groupBy(BOOK.AUTHOR_ID)
            )

        return dsl.with(maxYearCte)
            .select(
                BOOK.ID, BOOK.TITLE, BOOK.AUTHOR_ID, BOOK.PUBLISHED_YEAR,
                AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME
            )
            .from(BOOK)
            .join(AUTHOR).on(BOOK.AUTHOR_ID.eq(AUTHOR.ID))
            .join(maxYearCte)
                .on(
                    BOOK.AUTHOR_ID.eq(maxYearCte.field("author_id", Int::class.java))
                        .and(BOOK.PUBLISHED_YEAR.eq(maxYearCte.field("max_y", Int::class.java)))
                )
            .orderBy(AUTHOR.LAST_NAME.asc(), BOOK.TITLE.asc())
            .fetchInto(RecentBook::class.java)
    }
}
