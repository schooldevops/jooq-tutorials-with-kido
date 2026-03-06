package com.example.demo.repository;

import com.example.jooq.tables.records.AuthorRecord;
import com.example.jooq.tables.records.BookRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static com.example.jooq.Tables.AUTHOR;
import static com.example.jooq.Tables.BOOK;

@Repository
@RequiredArgsConstructor
public class JooqInsertRepository {

    private final DSLContext dsl;

    /**
     * 【DSL 스타일】 작가를 삽입하고 영향받은 행 수를 반환합니다.
     * SQL: INSERT INTO author (first_name, last_name) VALUES (?, ?)
     */
    public int insertAuthorDsl(String firstName, String lastName) {
        return dsl.insertInto(AUTHOR)
                  .set(AUTHOR.FIRST_NAME, firstName)
                  .set(AUTHOR.LAST_NAME, lastName)
                  .execute();
    }

    /**
     * 【UpdatableRecord 스타일】 작가 레코드를 생성하고, store() 호출 후 generated id가 채워진 레코드를 반환합니다.
     * SQL: INSERT INTO author (first_name, last_name) VALUES (?, ?) RETURNING *
     */
    public AuthorRecord insertAuthorWithRecord(String firstName, String lastName) {
        AuthorRecord record = dsl.newRecord(AUTHOR);
        record.setFirstName(firstName);
        record.setLastName(lastName);
        record.store(); // INSERT 실행 + DB generated 값(id)이 record에 자동 채워짐
        return record;
    }

    /**
     * 【INSERT ... RETURNING】 책을 삽입하고, DB가 생성한 BOOK.ID를 즉시 반환합니다.
     * SQL: INSERT INTO book (title, author_id, published_year) VALUES (?, ?, ?) RETURNING id
     */
    public Integer insertBookReturningId(String title, int authorId, int publishedYear) {
        BookRecord result = dsl.insertInto(BOOK)
                                .set(BOOK.TITLE, title)
                                .set(BOOK.AUTHOR_ID, authorId)
                                .set(BOOK.PUBLISHED_YEAR, publishedYear)
                                .returning(BOOK.ID)
                                .fetchOne();
        return result != null ? result.getId() : null;
    }
}
