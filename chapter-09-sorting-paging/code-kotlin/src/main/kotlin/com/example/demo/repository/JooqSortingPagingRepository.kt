package com.example.demo.repository

import com.example.jooq.tables.pojos.Book
import com.example.jooq.tables.references.BOOK
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class JooqSortingPagingRepository(
    private val dsl: DSLContext
) {

    /**
     * 【단일 컬럼 오름차순 정렬】 제목 오름차순으로 전체 책 목록 반환.
     */
    fun findBooksOrderedByTitle(): List<Book> =
        dsl.selectFrom(BOOK)
            .orderBy(BOOK.TITLE.asc())
            .fetchInto(Book::class.java)

    /**
     * 【내림차순 정렬】 출판연도 내림차순 (최신순) 정렬.
     */
    fun findBooksOrderedByYearDesc(): List<Book> =
        dsl.selectFrom(BOOK)
            .orderBy(BOOK.PUBLISHED_YEAR.desc())
            .fetchInto(Book::class.java)

    /**
     * 【Offset 페이징】 page(0-based) × size로 슬라이싱.
     */
    fun findBooksWithPaging(page: Int, size: Int): List<Book> =
        dsl.selectFrom(BOOK)
            .orderBy(BOOK.TITLE.asc())
            .limit(size)
            .offset(page.toLong() * size)
            .fetchInto(Book::class.java)

    /**
     * 【다중 정렬 + 페이징】 출판연도 내림차순 → 제목 오름차순 + 페이징.
     */
    fun findBooksWithMultiSort(page: Int, size: Int): List<Book> =
        dsl.selectFrom(BOOK)
            .orderBy(
                BOOK.PUBLISHED_YEAR.desc(),
                BOOK.TITLE.asc()
            )
            .limit(size)
            .offset(page.toLong() * size)
            .fetchInto(Book::class.java)
}
