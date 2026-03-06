package com.example.demo.repository

import com.example.jooq.tables.pojos.Author
import com.example.jooq.tables.records.AuthorRecord
import com.example.jooq.tables.references.AUTHOR
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class JooqSelectRepository(
    private val dsl: DSLContext
) {
    /**
     * 전체 작가 레코드를 조회합니다.
     */
    fun findAllAuthors(): List<AuthorRecord> {
        return dsl.selectFrom(AUTHOR)
            .fetch()
    }

    /**
     * 이름(성) 접두어와 최소 ID 조건으로 작가 구조체(POJO/DataClass) 리스트를 조회합니다.
     */
    fun findAuthorsByLastNameAndId(namePrefix: String, minId: Int): List<Author> {
        return dsl.selectFrom(AUTHOR)
            .where(
                AUTHOR.LAST_NAME.like("$namePrefix%")
                    .and(AUTHOR.ID.gt(minId))
            )
            .fetchInto(Author::class.java)
    }
}
