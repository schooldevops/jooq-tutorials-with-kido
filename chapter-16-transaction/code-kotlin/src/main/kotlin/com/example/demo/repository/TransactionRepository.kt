package com.example.demo.repository

import com.example.jooq.tables.references.AUTHOR
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

/**
 * 【Chapter 16】 트랜잭션과 스프링 통합 - Kotlin
 * 순수 I/O를 담당하는 레포지토리
 */
@Repository
class TransactionRepository(
    private val dsl: DSLContext
) {

    fun saveAuthor(id: Int, firstName: String, lastName: String) {
        dsl.insertInto(AUTHOR)
            .set(AUTHOR.ID, id)
            .set(AUTHOR.FIRST_NAME, firstName)
            .set(AUTHOR.LAST_NAME, lastName)
            .execute()
    }

    fun findAuthorNameById(id: Int): String? {
        return dsl.select(AUTHOR.FIRST_NAME)
            .from(AUTHOR)
            .where(AUTHOR.ID.eq(id))
            .fetchOptionalInto(String::class.java)
            .orElse(null)
    }
}
