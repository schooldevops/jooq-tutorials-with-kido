package com.example.demo.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.example.jooq.Tables.AUTHOR;

/**
 * 【Chapter 16】 트랜잭션과 스프링 통합
 * 순수 DB I/O만 담당하는 Repository 계층
 */
@Repository
@RequiredArgsConstructor
public class TransactionRepository {

    private final DSLContext dsl;

    /**
     * 저자 정보를 DB에 INSRT 합니다.
     */
    public void saveAuthor(int id, String firstName, String lastName) {
        dsl.insertInto(AUTHOR)
           .set(AUTHOR.ID, id)
           .set(AUTHOR.FIRST_NAME, firstName)
           .set(AUTHOR.LAST_NAME, lastName)
           .execute();
    }

    /**
     * 검증용: 특정 ID의 저자가 존재하는지 확인합니다.
     */
    public Optional<String> findAuthorNameById(int id) {
        return dsl.select(AUTHOR.FIRST_NAME)
                  .from(AUTHOR)
                  .where(AUTHOR.ID.eq(id))
                  .fetchOptionalInto(String.class);
    }
}
