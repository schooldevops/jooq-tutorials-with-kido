package com.example.demo.repository

import com.example.jooq.tables.pojos.Author
import com.example.jooq.tables.records.AuthorRecord
import com.example.jooq.tables.references.AUTHOR
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

/**
 * 【Chapter 17】 배치(Batch) 처리와 성능 - Kotlin
 */
@Repository
class BatchRepository(
    private val dsl: DSLContext
) {
    /**
     * 단건 Insert 반복
     */
    fun insertSingleBySingle(authors: List<Author>) {
        authors.forEach { author ->
            dsl.insertInto(AUTHOR)
                .set(AUTHOR.ID, author.id)
                .set(AUTHOR.FIRST_NAME, author.firstName)
                .set(AUTHOR.LAST_NAME, author.lastName)
                .execute()
        }
    }

    /**
     * jOOQ batchInsert() 사용
     */
    fun insertInBatch(authors: List<Author>) {
        val records: List<AuthorRecord> = authors.map { a ->
            val record: AuthorRecord = dsl.newRecord(AUTHOR)
            record.id = a.id
            record.firstName = a.firstName
            record.lastName = a.lastName
            record
        }

        dsl.batchInsert(records).execute()
    }

    /**
     * 데이터 정리용
     */
    fun deleteAllGenerated(startingId: Int) {
        dsl.deleteFrom(AUTHOR)
            .where(AUTHOR.ID.ge(startingId))
            .execute()
    }
}
