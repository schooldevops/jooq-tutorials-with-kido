package com.example.demo.repository;

import com.example.jooq.tables.pojos.Author;
import com.example.jooq.tables.records.AuthorRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.example.jooq.Tables.AUTHOR;

/**
 * 【Chapter 10 기초 프로젝트】 회원(Member) CRUD 관리자 Repository
 * Phase 1(05~09강)에서 배운 모든 기법을 통합합니다.
 *   - findAll  : SELECT + orderBy + limit/offset (09강)
 *   - findById : SELECT WHERE (05강)
 *   - create   : INSERT UpdatableRecord (06강)
 *   - update   : DSL UPDATE (07강)
 *   - delete   : Hard Delete (07강)
 */
@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final DSLContext dsl;

    /** 목록 조회: lastName 오름차순 정렬 + 페이징 */
    public List<Author> findAll(int page, int size) {
        return dsl.selectFrom(AUTHOR)
                  .orderBy(AUTHOR.LAST_NAME.asc())
                  .limit(size)
                  .offset((long) page * size)
                  .fetchInto(Author.class);
    }

    /** 단건 조회: ID로 조회, 없으면 Optional.empty() */
    public Optional<Author> findById(int id) {
        return Optional.ofNullable(
            dsl.selectFrom(AUTHOR)
               .where(AUTHOR.ID.eq(id))
               .fetchOneInto(Author.class)
        );
    }

    /** 회원 등록: UpdatableRecord.store() → 자동 생성 ID 포함 레코드 반환 */
    public AuthorRecord create(String firstName, String lastName) {
        AuthorRecord record = dsl.newRecord(AUTHOR);
        record.setFirstName(firstName);
        record.setLastName(lastName);
        record.store();
        return record;
    }

    /** 회원 수정: DSL UPDATE → 영향 행 수 반환 */
    public int update(int id, String firstName, String lastName) {
        return dsl.update(AUTHOR)
                  .set(AUTHOR.FIRST_NAME, firstName)
                  .set(AUTHOR.LAST_NAME, lastName)
                  .where(AUTHOR.ID.eq(id))
                  .execute();
    }

    /** 회원 삭제: Hard Delete → 영향 행 수 반환 */
    public int delete(int id) {
        return dsl.deleteFrom(AUTHOR)
                  .where(AUTHOR.ID.eq(id))
                  .execute();
    }
}
