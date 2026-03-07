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
@Sql(scripts = ["/test-data.sql"],
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class JooqSortingPagingRepositoryTest {

    @Autowired
    private lateinit var repository: JooqSortingPagingRepository

    @Test
    @DisplayName("findBooksOrderedByTitle: 제목 오름차순 정렬 시 순서가 올바르다")
    fun findBooksOrderedByTitleTest() {
        // when
        val books: List<Book> = repository.findBooksOrderedByTitle()

        // then
        assertThat(books).isNotNull.isNotEmpty
        for (i in 1 until books.size) {
            assertThat(books[i].title).isGreaterThanOrEqualTo(books[i - 1].title)
        }
    }

    @Test
    @DisplayName("findBooksOrderedByYearDesc: 출판연도 내림차순 정렬 시 최신 책이 먼저다")
    fun findBooksOrderedByYearDescTest() {
        // when
        val books: List<Book> = repository.findBooksOrderedByYearDesc()

        // then
        assertThat(books).isNotNull.isNotEmpty
        for (i in 1 until books.size) {
            val prev = books[i - 1].publishedYear
            val curr = books[i].publishedYear
            if (prev != null && curr != null) {
                assertThat(curr).isLessThanOrEqualTo(prev)
            }
        }
    }

    @Test
    @DisplayName("findBooksWithPaging(0, 2): 첫 페이지에 최대 2건만 반환된다")
    fun findBooksWithPagingTest() {
        // when
        val books: List<Book> = repository.findBooksWithPaging(0, 2)

        // then
        assertThat(books).isNotNull
        assertThat(books.size).isLessThanOrEqualTo(2)
    }

    @Test
    @DisplayName("findBooksWithMultiSort(0, 3): 다중 정렬+페이징 적용 시 최대 3건 반환된다")
    fun findBooksWithMultiSortTest() {
        // when
        val books: List<Book> = repository.findBooksWithMultiSort(0, 3)

        // then
        assertThat(books).isNotNull
        assertThat(books.size).isLessThanOrEqualTo(3)
        for (i in 1 until books.size) {
            val prev = books[i - 1].publishedYear
            val curr = books[i].publishedYear
            if (prev != null && curr != null) {
                assertThat(curr).isLessThanOrEqualTo(prev)
            }
        }
    }
}
