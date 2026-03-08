package com.example.demo.repository

import com.example.demo.dto.BookRank
import com.example.demo.dto.YearlyRunningTotal
import com.example.jooq.tables.references.BOOK
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository

/**
 * 【Chapter 14】 윈도우 함수 및 분석용 SQL - Kotlin
 */
@Repository
class WindowFunctionRepository(
    private val dsl: DSLContext
) {

    /**
     * RANK() OVER (PARTITION BY author_id ORDER BY published_year DESC)
     */
    fun rankBooksByYearPerAuthor(): List<BookRank> {
        val rankField = DSL.rank()
            .over(DSL.partitionBy(BOOK.AUTHOR_ID).orderBy(BOOK.PUBLISHED_YEAR.desc()))
            .`as`("rank_in_author")

        return dsl.select(
                BOOK.ID, BOOK.TITLE, BOOK.AUTHOR_ID, BOOK.PUBLISHED_YEAR, rankField
            )
            .from(BOOK)
            .orderBy(BOOK.AUTHOR_ID.asc(), DSL.field("rank_in_author").asc())
            .fetchInto(BookRank::class.java)
    }

    /**
     * SUM(COUNT) OVER (ORDER BY published_year) - 연도별 누적합
     */
    fun runningTotalByYear(): List<YearlyRunningTotal> {
        val bookCount = DSL.count().`as`("book_count")
        val runningTotal = DSL.sum(DSL.count())
                              .over(DSL.orderBy(BOOK.PUBLISHED_YEAR.asc()))
                              .`as`("running_total")

        return dsl.select(BOOK.PUBLISHED_YEAR, bookCount, runningTotal)
            .from(BOOK)
            .groupBy(BOOK.PUBLISHED_YEAR)
            .orderBy(BOOK.PUBLISHED_YEAR.asc())
            .fetchInto(YearlyRunningTotal::class.java)
    }

    /**
     * RANK() + CTE: 저자별 rank=1(최신) 책만 추출
     */
    fun findTopRankedBookPerAuthor(): List<BookRank> {
        val rankField = DSL.rank()
            .over(DSL.partitionBy(BOOK.AUTHOR_ID).orderBy(BOOK.PUBLISHED_YEAR.desc()))
            .`as`("rank_in_author")

        val rankedCte = DSL.name("ranked")
            .fields("id", "title", "author_id", "published_year", "rank_in_author")
            .`as`(
                DSL.select(BOOK.ID, BOOK.TITLE, BOOK.AUTHOR_ID, BOOK.PUBLISHED_YEAR, rankField)
                    .from(BOOK)
            )

        return dsl.with(rankedCte)
            .selectFrom(rankedCte)
            .where(rankedCte.field("rank_in_author", Int::class.java)!!.eq(1))
            .orderBy(rankedCte.field("author_id"))
            .fetchInto(BookRank::class.java)
    }
}
