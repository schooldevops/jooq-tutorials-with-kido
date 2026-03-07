package com.example.demo.repository;

import com.example.jooq.tables.records.AuthorRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import static com.example.jooq.Tables.AUTHOR;
import static com.example.jooq.Tables.BOOK;

@Repository
@RequiredArgsConstructor
public class JooqUpdateDeleteRepository {

    private final DSLContext dsl;

    /**
     * 【DSL 스타일 UPDATE】 책 제목을 수정하고 영향받은 행 수를 반환합니다.
     * SQL: UPDATE book SET title = ? WHERE id = ?
     */
    public int updateBookTitle(int bookId, String newTitle) {
        return dsl.update(BOOK)
                  .set(BOOK.TITLE, newTitle)
                  .where(BOOK.ID.eq(bookId))
                  .execute();
    }

    /**
     * 【UpdatableRecord 스타일 UPDATE】 작가를 조회 후 이름을 수정하고 저장된 레코드를 반환합니다.
     * jOOQ는 PK가 있는 레코드에 store()를 호출하면 자동으로 UPDATE를 수행합니다.
     */
    public AuthorRecord updateAuthorWithRecord(int authorId, String newFirstName) {
        AuthorRecord record = dsl.fetchOne(AUTHOR, AUTHOR.ID.eq(authorId));
        if (record == null) throw new IllegalArgumentException("Author not found: " + authorId);
        record.setFirstName(newFirstName);
        record.store(); // PK 존재 → 자동 UPDATE
        return record;
    }

    /**
     * 【Hard Delete (물리 삭제)】 책을 완전히 삭제하고 영향받은 행 수를 반환합니다.
     * SQL: DELETE FROM book WHERE id = ?
     */
    public int deleteBookById(int bookId) {
        return dsl.deleteFrom(BOOK)
                  .where(BOOK.ID.eq(bookId))
                  .execute();
    }

    /**
     * 【Soft Delete (논리 삭제)】 deleted_at 컬럼에 현재 시각을 기록합니다.
     * SQL: UPDATE book SET deleted_at = NOW() WHERE id = ? AND deleted_at IS NULL
     */
    public int softDeleteBook(int bookId) {
        return dsl.update(BOOK)
                  .set(BOOK.DELETED_AT, LocalDateTime.now())
                  .where(BOOK.ID.eq(bookId))
                  .and(BOOK.DELETED_AT.isNull()) // 이미 삭제된 것 방지 (멱등성)
                  .execute();
    }
}
