package com.example.demo.repository

import com.example.demo.dto.AuthorBookCount
import com.example.demo.dto.RecentBook
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
class SubqueryCteRepositoryTest {

    @Autowired
    private lateinit var repository: SubqueryCteRepository

    @Test
    @DisplayName("Scalar Subquery: 평균 출판연도보다 이후 책만 반환된다")
    fun findBooksAboveAvgYear_ReturnsOnlyAboveAverage() {
        // when
        val books: List<Book> = repository.findBooksAboveAvgYear()

        // then: 최신순 정렬 확인
        assertThat(books).isNotEmpty()
        for (i in 0 until books.size - 1) {
            assertThat(books[i].publishedYear)
                .isGreaterThanOrEqualTo(books[i + 1].publishedYear)
        }
    }

    @Test
    @DisplayName("Scalar Subquery: CteBook D(2022)는 결과에 반드시 포함된다")
    fun findBooksAboveAvgYear_IncludesLatestBook() {
        val books: List<Book> = repository.findBooksAboveAvgYear()
        val hasLatest = books.any { it.title == "CteBook D" }
        assertThat(hasLatest).isTrue()
    }

    @Test
    @DisplayName("CTE: 저자별 책 수가 올바르게 집계된다")
    fun findAuthorsWithBookCount_CountsCorrectly() {
        val result: List<AuthorBookCount> = repository.findAuthorsWithBookCount()

        assertThat(result).isNotEmpty()
        assertThat(result).allMatch { it.cnt != null && it.cnt >= 1 }

        // CteTest 저자들 각각 2권씩
        result.filter { it.firstName == "CteTest" }
              .forEach { assertThat(it.cnt).isEqualTo(2) }
    }

    @Test
    @DisplayName("CTE + JOIN: 저자별 최신 책만 반환된다")
    fun findRecentBooksPerAuthor_ReturnsLatestPerAuthor() {
        val result: List<RecentBook> = repository.findRecentBooksPerAuthor()

        // AuthorX 최신 = CteBook B(2015)
        val hasAuthorXLatest = result.any { it.title == "CteBook B" && it.publishedYear == 2015 }
        assertThat(hasAuthorXLatest).isTrue()

        // AuthorY 최신 = CteBook D(2022)
        val hasAuthorYLatest = result.any { it.title == "CteBook D" && it.publishedYear == 2022 }
        assertThat(hasAuthorYLatest).isTrue()
    }

    @Test
    @DisplayName("CTE + JOIN: 저자 이름도 함께 조회된다")
    fun findRecentBooksPerAuthor_IncludesAuthorName() {
        val result: List<RecentBook> = repository.findRecentBooksPerAuthor()

        result.filter { it.title?.startsWith("CteBook") == true }
              .forEach { assertThat(it.firstName).isNotNull() }
    }
}
