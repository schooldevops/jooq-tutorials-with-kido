package com.example.demo.repository

import com.example.jooq.tables.pojos.Book
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
@Sql(scripts = ["/test-data.sql"], executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class DynamicSqlRepositoryTest {

    @Autowired
    private lateinit var repository: DynamicSqlRepository

    @Test
    @DisplayName("searchBooks: 파라미터가 모두 null이면 전체 도서를 조회한다")
    fun searchBooks_AllNull() {
        // when
        val books: List<Book> = repository.searchBooks(null, null, null)

        // then: 기본 3건 + 테스트 데이터 5건 = 8건 이상
        assertThat(books).isNotEmpty
        assertThat(books.size).isGreaterThanOrEqualTo(8)
    }

    @Test
    @DisplayName("searchBooks: title만 전달하면 LIKE 조건으로 필터링된다")
    fun searchBooks_TitleOnly() {
        // when: 대소문자 무시 (ILike)
        val books: List<Book> = repository.searchBooks("dynamicHamlet", null, null)

        // then
        assertThat(books).hasSize(1)
        assertThat(books[0].title).isEqualTo("DynamicHamlet")
    }

    @Test
    @DisplayName("searchBooks: authorId만 전달하면 해당 저자 도서만 조회된다")
    fun searchBooks_AuthorIdOnly() {
        // given: DynamicShakespeare (id=101)
        val authorId = 101

        // when
        val books: List<Book> = repository.searchBooks(null, authorId, null)

        // then: DynamicHamlet, DynamicRomeo and Juliet
        assertThat(books).hasSize(3)
        assertThat(books).allMatch { book -> book.authorId == 101 }
    }

    @Test
    @DisplayName("searchBooks: title+year 복합 조건 모두 만족하는 도서만 조회된다")
    fun searchBooks_ComplexCondition() {
        // when: DynamicShakespeare 저자의 DynamicRomeo and Juliet
        val books: List<Book> = repository.searchBooks("DynamicRomeo", 101, 1597)

        // then
        assertThat(books).hasSize(1)
        assertThat(books[0].title).isEqualTo("DynamicRomeo and Juliet")
        assertThat(books[0].publishedYear).isEqualTo(1597)
    }

    @Test
    @DisplayName("searchBooksWithNoCondition: title, year 조건에 맞는 도서를 반환한다")
    fun searchBooksWithNoCondition_Test() {
        // when: Dynamic 시리즈 중 2011년도
        val books: List<Book> = repository.searchBooksWithNoCondition("dynamic", 2011)

        // then: The Dynamic Coder
        assertThat(books).hasSize(1)
        assertThat(books[0].title).isEqualTo("The Dynamic Coder")
    }
}
