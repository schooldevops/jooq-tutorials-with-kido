package com.example.demo.repository;

import com.example.jooq.tables.pojos.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class DynamicSqlRepositoryTest {

    @Autowired
    private DynamicSqlRepository repository;

    @Test
    @DisplayName("searchBooks: 파라미터가 모두 null이면 전체 도서를 조회한다")
    void searchBooks_AllNull() {
        // when
        List<Book> books = repository.searchBooks(null, null, null);

        // then: data.sql 기본 3건 + test-data.sql 5건 = 8건 이상
        assertThat(books).isNotEmpty();
        assertThat(books.size()).isGreaterThanOrEqualTo(8);
    }

    @Test
    @DisplayName("searchBooks: title만 전달하면 LIKE 조건으로 필터링된다")
    void searchBooks_TitleOnly() {
        // when: 대소문자 무시 검색 검증
        List<Book> books = repository.searchBooks("dynamicHamlet", null, null);

        // then
        assertThat(books).hasSize(1);
        assertThat(books.get(0).getTitle()).isEqualTo("DynamicHamlet");
    }

    @Test
    @DisplayName("searchBooks: authorId만 전달하면 해당 저자 도서만 조회된다")
    void searchBooks_AuthorIdOnly() {
        // given: DynamicShakespeare (id=101)
        Integer authorId = 101;

        // when
        List<Book> books = repository.searchBooks(null, authorId, null);

        // then: DynamicHamlet(1600), DynamicRomeo and Juliet(1597), The Dynamic Coder(2011)
        assertThat(books).hasSize(3);
        assertThat(books).allMatch(book -> book.getAuthorId().equals(101));
    }

    @Test
    @DisplayName("searchBooks: title+year 복합 조건 모두 만족하는 도서만 조회된다")
    void searchBooks_ComplexCondition() {
        // when: DynamicShakespeare 저자의 DynamicRomeo and Juliet
        List<Book> books = repository.searchBooks("DynamicRomeo", 101, 1597);

        // then
        assertThat(books).hasSize(1);
        assertThat(books.get(0).getTitle()).isEqualTo("DynamicRomeo and Juliet");
        assertThat(books.get(0).getPublishedYear()).isEqualTo(1597);
    }

    @Test
    @DisplayName("searchBooksWithNoCondition: title, year 조건에 맞는 도서를 반환한다")
    void searchBooksWithNoCondition_Test() {
        // when: Dynamic 시리즈 책 중 2011년도
        List<Book> books = repository.searchBooksWithNoCondition("dynamic", 2011);

        // then: The Dynamic Coder
        assertThat(books).hasSize(1);
        assertThat(books.get(0).getTitle()).isEqualTo("The Dynamic Coder");
    }
}
