package com.example.demo.repository;

import com.example.jooq.tables.records.AuthorRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static com.example.jooq.Tables.BOOK;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional // 각 테스트 종료 후 자동 롤백
class JooqUpdateDeleteRepositoryTest {

    @Autowired
    private JooqUpdateDeleteRepository repository;

    @Autowired
    private org.jooq.DSLContext dsl;

    @Test
    @DisplayName("DSL 스타일로 책 제목을 수정하면 영향받은 행 수가 1이다")
    void updateBookTitleTest() {
        // given: init-script로 seeded된 book (id=1, title="Hamlet")
        int bookId = 1;
        String newTitle = "Hamlet (2nd Edition)";

        // when
        int affected = repository.updateBookTitle(bookId, newTitle);

        // then
        assertThat(affected).isEqualTo(1);
        String updatedTitle = dsl.select(BOOK.TITLE).from(BOOK).where(BOOK.ID.eq(bookId)).fetchOne(BOOK.TITLE);
        assertThat(updatedTitle).isEqualTo(newTitle);
    }

    @Test
    @DisplayName("UpdatableRecord로 작가 이름을 수정하면 변경된 레코드가 반환된다")
    void updateAuthorWithRecordTest() {
        // given: init-script로 seeded된 author (id=1, firstName="William")
        int authorId = 1;
        String newFirstName = "Bill";

        // when
        AuthorRecord record = repository.updateAuthorWithRecord(authorId, newFirstName);

        // then
        assertThat(record).isNotNull();
        assertThat(record.getFirstName()).isEqualTo(newFirstName);
        assertThat(record.getId()).isEqualTo(authorId);
    }

    @Test
    @DisplayName("Hard Delete로 책을 삭제하면 DB에서 완전히 제거된다")
    void deleteBookByIdTest() {
        // given: init-script 데이터 (book id=2, "Pride and Prejudice")
        int bookId = 2;

        // when
        int affected = repository.deleteBookById(bookId);

        // then
        assertThat(affected).isEqualTo(1);
        var result = dsl.fetchOne(BOOK, BOOK.ID.eq(bookId));
        assertThat(result).isNull(); // 완전히 삭제됨
    }

    @Test
    @DisplayName("Soft Delete로 책을 삭제하면 deleted_at이 설정되고 DB에는 남아있다")
    void softDeleteBookTest() {
        // given: init-script 데이터 (book id=1, "Hamlet")
        int bookId = 1;

        // when
        int affected = repository.softDeleteBook(bookId);

        // then
        assertThat(affected).isEqualTo(1);
        var book = dsl.fetchOne(BOOK, BOOK.ID.eq(bookId));
        assertThat(book).isNotNull();          // 레코드는 DB에 그대로 존재
        assertThat(book.getDeletedAt()).isNotNull(); // deleted_at이 설정됨
    }
}
