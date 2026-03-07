package com.example.demo.repository

import com.example.jooq.tables.pojos.Author
import com.example.jooq.tables.records.AuthorRecord
import com.example.jooq.tables.references.AUTHOR
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

/**
 * 【Chapter 10 기초 프로젝트】 회원(Member) CRUD 관리자 Repository (Kotlin)
 * Phase 1(05~09강)에서 배운 모든 기법을 통합합니다.
 */
@Repository
class MemberRepository(
    private val dsl: DSLContext
) {

    /** 목록 조회: lastName 오름차순 정렬 + 페이징 */
    fun findAll(page: Int, size: Int): List<Author> =
        dsl.selectFrom(AUTHOR)
            .orderBy(AUTHOR.LAST_NAME.asc())
            .limit(size)
            .offset(page.toLong() * size)
            .fetchInto(Author::class.java)

    /** 단건 조회: ID로 조회, 없으면 null */
    fun findById(id: Int): Author? =
        dsl.selectFrom(AUTHOR)
            .where(AUTHOR.ID.eq(id))
            .fetchOneInto(Author::class.java)

    /** 회원 등록: UpdatableRecord.store() → 자동 생성 ID 포함 레코드 반환 */
    fun create(firstName: String, lastName: String): AuthorRecord {
        val record = dsl.newRecord(AUTHOR)
        record.firstName = firstName
        record.lastName = lastName
        record.store()
        return record
    }

    /** 회원 수정: DSL UPDATE → 영향 행 수 반환 */
    fun update(id: Int, firstName: String, lastName: String): Int =
        dsl.update(AUTHOR)
            .set(AUTHOR.FIRST_NAME, firstName)
            .set(AUTHOR.LAST_NAME, lastName)
            .where(AUTHOR.ID.eq(id))
            .execute()

    /** 회원 삭제: Hard Delete → 영향 행 수 반환 */
    fun delete(id: Int): Int =
        dsl.deleteFrom(AUTHOR)
            .where(AUTHOR.ID.eq(id))
            .execute()
}
