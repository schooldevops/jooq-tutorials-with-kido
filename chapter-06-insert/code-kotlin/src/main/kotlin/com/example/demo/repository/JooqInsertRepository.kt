package com.example.demo.repository

import com.example.jooq.tables.records.AuthorRecord
import com.example.jooq.tables.records.BookRecord
import com.example.jooq.tables.references.AUTHOR
import com.example.jooq.tables.references.BOOK
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class JooqInsertRepository(
    private val dsl: DSLContext
) {

    /**
     * 【DSL 스타일】 작가를 삽입하고 영향받은 행 수를 반환합니다.
     * SQL: INSERT INTO author (first_name, last_name) VALUES (?, ?)
     */
    fun insertAuthorDsl(firstName: String, lastName: String): Int {
        return dsl.insertInto(AUTHOR)
            .set(AUTHOR.FIRST_NAME, firstName)
            .set(AUTHOR.LAST_NAME, lastName)
            .execute()
    }

    /**
     * 【UpdatableRecord 스타일】 작가 레코드를 생성하고, store() 호출 후 generated id가 채워진 레코드를 반환합니다.
     * Kotlin의 apply {} 블록으로 더 간결하게 초기화합니다.
     */
    fun insertAuthorWithRecord(firstName: String, lastName: String): AuthorRecord {
        return dsl.newRecord(AUTHOR).apply {
            this.firstName = firstName
            this.lastName = lastName
        }.also { it.store() } // store() 실행 후 DB generated 값이 레코드에 자동 채워짐
    }

    /**
     * 【INSERT ... RETURNING】 책을 삽입하고, DB가 생성한 BOOK.ID를 즉시 반환합니다.
     * SQL: INSERT INTO book (...) VALUES (...) RETURNING id
     */
    fun insertBookReturningId(title: String, authorId: Int, publishedYear: Int): Int? {
        return dsl.insertInto(BOOK)
            .set(BOOK.TITLE, title)
            .set(BOOK.AUTHOR_ID, authorId)
            .set(BOOK.PUBLISHED_YEAR, publishedYear)
            .returning(BOOK.ID)
            .fetchOne()
            ?.id
    }
}
