package com.example.demo.repository

import com.example.demo.dto.BookWithAuthor
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
class JoinRepositoryTest {

    @Autowired
    private lateinit var repository: JoinRepository

    @Test
    @DisplayName("INNER JOIN: 저자가 있는 책만 반환된다")
    fun findBooksWithAuthor_InnerJoin() {
        // when
        val books: List<BookWithAuthor> = repository.findBooksWithAuthor()

        // then: JoinBook 시리즈 중 저자 있는 것 포함
        val hasJoinTestBooks = books.any { it.title?.startsWith("JoinBook") == true }
        assertThat(hasJoinTestBooks).isTrue()

        // INNER JOIN이므로 firstName이 null인 행 없어야 함
        val hasNullFirstName = books
            .filter { it.title?.startsWith("JoinBook") == true }
            .any { it.firstName == null }
        assertThat(hasNullFirstName).isFalse()
    }

    @Test
    @DisplayName("INNER JOIN: 저자 없는 책(JoinBook Delta)은 결과에서 제외된다")
    fun findBooksWithAuthor_ExcludesNullAuthor() {
        // when
        val books: List<BookWithAuthor> = repository.findBooksWithAuthor()

        // then
        val hasDelta = books.any { it.title == "JoinBook Delta" }
        assertThat(hasDelta).isFalse()
    }

    @Test
    @DisplayName("LEFT JOIN: 저자 없는 책(JoinBook Delta)도 결과에 포함된다")
    fun findAllBooksWithAuthor_LeftJoin_IncludesNullAuthor() {
        // when
        val books: List<BookWithAuthor> = repository.findAllBooksWithAuthor()

        // then: JoinBook Delta 포함
        val delta = books.find { it.title == "JoinBook Delta" }
        assertThat(delta).isNotNull
        assertThat(delta?.firstName).isNull()
    }

    @Test
    @DisplayName("LEFT JOIN 결과는 INNER JOIN 결과보다 같거나 많다")
    fun leftJoin_CountGreaterThanOrEqualToInnerJoin() {
        val innerJoin = repository.findBooksWithAuthor()
        val leftJoin = repository.findAllBooksWithAuthor()
        assertThat(leftJoin.size).isGreaterThanOrEqualTo(innerJoin.size)
    }

    @Test
    @DisplayName("INNER JOIN + WHERE: 특정 연도 이후 책만 저자와 함께 반환된다")
    fun findBooksAfterYearWithAuthor_FilteredByYear() {
        // when: 2010년 이후
        val books: List<BookWithAuthor> = repository.findBooksAfterYearWithAuthor(2010)

        // then
        val hasBeta = books.any { it.title == "JoinBook Beta" }
        val hasGamma = books.any { it.title == "JoinBook Gamma" }
        val hasAlpha = books.any { it.title == "JoinBook Alpha" }

        assertThat(hasBeta).isTrue()
        assertThat(hasGamma).isTrue()
        assertThat(hasAlpha).isFalse() // 2000년 → 제외

        // 모든 JoinBook 결과의 연도 >= 2010 검증
        books.filter { it.title?.startsWith("JoinBook") == true }
            .forEach { assertThat(it.publishedYear).isGreaterThanOrEqualTo(2010) }
    }
}
