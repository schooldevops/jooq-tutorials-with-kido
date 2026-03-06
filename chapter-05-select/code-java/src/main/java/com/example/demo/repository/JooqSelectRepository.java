package com.example.demo.repository;

import com.example.jooq.tables.pojos.Author;
import com.example.jooq.tables.records.AuthorRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.jooq.Tables.AUTHOR;

@Repository
@RequiredArgsConstructor
public class JooqSelectRepository {

    private final DSLContext dsl;

    /**
     * 전체 작가 레코드를 조회합니다.
     */
    public List<AuthorRecord> findAllAuthors() {
        return dsl.selectFrom(AUTHOR)
                  .fetch();
    }

    /**
     * 이름(성) 접두어와 최소 ID 조건으로 작가 POJO 리스트를 조회합니다.
     */
    public List<Author> findAuthorsByLastNameAndId(String namePrefix, Integer minId) {
        return dsl.selectFrom(AUTHOR)
                  .where(AUTHOR.LAST_NAME.like(namePrefix + "%")
                      .and(AUTHOR.ID.gt(minId)))
                  .fetchInto(Author.class);
    }
}
