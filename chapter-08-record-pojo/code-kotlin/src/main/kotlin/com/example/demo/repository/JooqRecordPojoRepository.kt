package com.example.demo.repository

import com.example.demo.dto.BookSummaryDto
import com.example.jooq.tables.pojos.Book
import com.example.jooq.tables.records.BookRecord
import com.example.jooq.tables.references.AUTHOR
import com.example.jooq.tables.references.BOOK
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class JooqRecordPojoRepository(
    private val dsl: DSLContext
) {

    /**
     * 【Record 타입】 BOOK 전체를 jOOQ 자동 생성 Record 타입으로 반환합니다.
     */
    fun fetchAllAsRecord(): List<BookRecord> {
        return dsl.selectFrom(BOOK).fetch()
    }

    /**
     * 【POJO 매핑】 BOOK 전체를 자동 생성 POJO(Book)로 변환하여 반환합니다.
     */
    fun fetchAllAsPojo(): List<Book> {
        return dsl.selectFrom(BOOK).fetchInto(Book::class.java)
    }

    /**
     * 【Map 변환】 BOOK.ID → BOOK.TITLE 형태의 Key-Value Map을 반환합니다.
     */
    fun fetchAsMap(): Map<Int?, String?> {
        return dsl.select(BOOK.ID, BOOK.TITLE)
            .from(BOOK)
            .fetchMap(BOOK.ID, BOOK.TITLE)
    }

    /**
     * 【커스텀 DTO 매핑】 AUTHOR와 BOOK을 JOIN한 뒤 Kotlin 람다로 BookSummaryDto로 직접 조립합니다.
     * Kotlin fetch { } 블록 = Java fetch(Function) 과 동일.
     */
    fun fetchIntoCustomDto(): List<BookSummaryDto> {
        return dsl.select(BOOK.ID, BOOK.TITLE, AUTHOR.LAST_NAME)
            .from(BOOK)
            .join(AUTHOR).on(BOOK.AUTHOR_ID.eq(AUTHOR.ID))
            .fetch { BookSummaryDto(it[BOOK.ID], it[BOOK.TITLE], it[AUTHOR.LAST_NAME]) }
    }
}
