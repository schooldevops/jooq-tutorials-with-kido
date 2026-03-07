package com.example.demo.repository

import com.example.jooq.tables.pojos.Book
import com.example.jooq.tables.references.BOOK
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository

/**
 * 【Chapter 11】 동적 SQL의 마법 (Kotlin)
 * Kotlin의 takeIf, let, fold를 활용하여 더욱 우아하게 동적 조건을 조립합니다.
 */
@Repository
class DynamicSqlRepository(
    private val dsl: DSLContext
) {

    /**
     * 패턴 1: mutableListOf + takeIf / let 조합
     * null이 아닌 파라미터만 판단하여 조건 리스트에 추가합니다.
     */
    fun searchBooks(title: String?, authorId: Int?, year: Int?): List<Book> {
        val conditions = mutableListOf<Condition>()

        title?.takeIf { it.isNotBlank() }
             ?.let { conditions.add(BOOK.TITLE.containsIgnoreCase(it)) }

        authorId?.let { conditions.add(BOOK.AUTHOR_ID.eq(it)) }

        year?.let { conditions.add(BOOK.PUBLISHED_YEAR.eq(it)) }

        return dsl.selectFrom(BOOK)
            .where(DSL.and(conditions)) // 빈 리스트인 경우 WHERE TRUE
            .orderBy(BOOK.TITLE.asc())
            .fetchInto(Book::class.java)
    }

    /**
     * 패턴 2: listOfNotNull + fold + noCondition() 조합
     * 가장 Kotlin다운 방식으로, 불변(immutable) 리스트를 조립 후 fold 연산으로 합칩니다.
     */
    fun searchBooksWithNoCondition(title: String?, year: Int?): List<Book> {
        val conditions = listOfNotNull(
            title?.takeIf { it.isNotBlank() }?.let { BOOK.TITLE.containsIgnoreCase(it) },
            year?.let { BOOK.PUBLISHED_YEAR.eq(it) }
        )

        // 초기값 noCondition()에 모든 Condition을 and로 누적 결합
        val where = conditions.fold(DSL.noCondition()) { acc, cond -> acc.and(cond) }

        return dsl.selectFrom(BOOK)
            .where(where)
            .orderBy(BOOK.TITLE.asc())
            .fetchInto(Book::class.java)
    }
}
