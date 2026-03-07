package com.example.demo.repository

import com.example.jooq.tables.references.BOOK
import org.assertj.core.api.Assertions.assertThat
import org.jooq.DSLContext
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional // 각 테스트 종료 후 자동 롤백
class JooqUpdateDeleteRepositoryTest {

    @Autowired
    private lateinit var repository: JooqUpdateDeleteRepository

    @Autowired
    private lateinit var dsl: DSLContext

    @Test
    @DisplayName("DSL 스타일로 책 제목을 수정하면 영향받은 행 수가 1이다")
    fun updateBookTitleTest() {
        // given: init-script로 seeded된 book (id=1, title="Hamlet")
        val bookId = 1
        val newTitle = "Hamlet (2nd Edition)"

        // when
        val affected = repository.updateBookTitle(bookId, newTitle)

        // then
        assertThat(affected).isEqualTo(1)
        val updatedTitle = dsl.select(BOOK.TITLE).from(BOOK).where(BOOK.ID.eq(bookId)).fetchOne(BOOK.TITLE)
        assertThat(updatedTitle).isEqualTo(newTitle)
    }

    @Test
    @DisplayName("UpdatableRecord로 작가 이름을 수정하면 변경된 레코드가 반환된다")
    fun updateAuthorWithRecordTest() {
        // given: init-script로 seeded된 author (id=1, firstName="William")
        val authorId = 1
        val newFirstName = "Bill"

        // when
        val record = repository.updateAuthorWithRecord(authorId, newFirstName)

        // then
        assertThat(record).isNotNull
        assertThat(record.firstName).isEqualTo(newFirstName)
        assertThat(record.id).isEqualTo(authorId)
    }

    @Test
    @DisplayName("Hard Delete로 책을 삭제하면 DB에서 완전히 제거된다")
    fun deleteBookByIdTest() {
        // given: init-script 데이터 (book id=2, "Pride and Prejudice")
        val bookId = 2

        // when
        val affected = repository.deleteBookById(bookId)

        // then
        assertThat(affected).isEqualTo(1)
        val result = dsl.fetchOne(BOOK, BOOK.ID.eq(bookId))
        assertThat(result).isNull() // 완전히 삭제됨
    }

    @Test
    @DisplayName("Soft Delete로 책을 삭제하면 deleted_at이 설정되고 DB에는 남아있다")
    fun softDeleteBookTest() {
        // given: init-script 데이터 (book id=1, "Hamlet")
        val bookId = 1

        // when
        val affected = repository.softDeleteBook(bookId)

        // then
        assertThat(affected).isEqualTo(1)
        val book = dsl.fetchOne(BOOK, BOOK.ID.eq(bookId))
        assertThat(book).isNotNull                      // 레코드는 DB에 그대로 존재
        assertThat(book!!.deletedAt).isNotNull()        // deleted_at이 설정됨
    }
}
