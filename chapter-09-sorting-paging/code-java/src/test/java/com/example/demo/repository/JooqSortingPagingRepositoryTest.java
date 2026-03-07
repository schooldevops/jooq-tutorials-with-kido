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
@Sql(scripts = "/test-data.sql",
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class JooqSortingPagingRepositoryTest {

    @Autowired
    private JooqSortingPagingRepository repository;

    @Test
    @DisplayName("findBooksOrderedByTitle: 제목 오름차순 정렬 시 첫 번째 요소가 알파벳 최솟값이다")
    void findBooksOrderedByTitleTest() {
        // when
        List<Book> books = repository.findBooksOrderedByTitle();

        // then
        assertThat(books).isNotNull().isNotEmpty();
        // 타이틀 오름차순 검증: 각 요소가 이전 요소보다 크거나 같아야 함
        for (int i = 1; i < books.size(); i++) {
            assertThat(books.get(i).getTitle())
                .isGreaterThanOrEqualTo(books.get(i - 1).getTitle());
        }
    }

    @Test
    @DisplayName("findBooksOrderedByYearDesc: 출판연도 내림차순 정렬 시 최신 책이 첫 번째다")
    void findBooksOrderedByYearDescTest() {
        // when
        List<Book> books = repository.findBooksOrderedByYearDesc();

        // then
        assertThat(books).isNotNull().isNotEmpty();
        // 연도 내림차순 검증
        for (int i = 1; i < books.size(); i++) {
            if (books.get(i - 1).getPublishedYear() != null && books.get(i).getPublishedYear() != null) {
                assertThat(books.get(i).getPublishedYear())
                    .isLessThanOrEqualTo(books.get(i - 1).getPublishedYear());
            }
        }
    }

    @Test
    @DisplayName("findBooksWithPaging(0, 2): 첫 페이지에 최대 2건만 반환된다")
    void findBooksWithPagingTest() {
        // when
        List<Book> books = repository.findBooksWithPaging(0, 2);

        // then
        assertThat(books).isNotNull();
        assertThat(books.size()).isLessThanOrEqualTo(2);
    }

    @Test
    @DisplayName("findBooksWithMultiSort(0, 3): 다중 정렬+페이징 적용 시 최대 3건 반환된다")
    void findBooksWithMultiSortTest() {
        // when
        List<Book> books = repository.findBooksWithMultiSort(0, 3);

        // then
        assertThat(books).isNotNull();
        assertThat(books.size()).isLessThanOrEqualTo(3);
        // 연도 내림차순 검증
        for (int i = 1; i < books.size(); i++) {
            if (books.get(i - 1).getPublishedYear() != null && books.get(i).getPublishedYear() != null) {
                assertThat(books.get(i).getPublishedYear())
                    .isLessThanOrEqualTo(books.get(i - 1).getPublishedYear());
            }
        }
    }
}
