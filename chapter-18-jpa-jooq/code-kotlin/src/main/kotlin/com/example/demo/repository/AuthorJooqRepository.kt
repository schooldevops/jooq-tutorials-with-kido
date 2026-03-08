package com.example.demo.repository

import com.example.jooq.tables.pojos.Author
import com.example.jooq.tables.references.AUTHOR
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

/**
 * 【Chapter 18】 CQRS Query(Read) 영역: Kotlin jOOQ
 */
@Repository
class AuthorJooqRepository(
    private val dsl: DSLContext
) {
    fun findById(id: Int): Author? {
        return dsl.selectFrom(AUTHOR)
            .where(AUTHOR.ID.eq(id))
            .fetchOptionalInto(Author::class.java)
            .orElse(null)
    }
}
