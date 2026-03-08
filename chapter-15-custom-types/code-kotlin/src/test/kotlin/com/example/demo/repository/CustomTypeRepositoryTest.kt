package com.example.demo.repository

import com.example.demo.dto.BookWithStatus
import com.example.demo.type.BookStatus
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
class CustomTypeRepositoryTest {

    @Autowired
    private lateinit var repository: CustomTypeRepository

    @Test
    @DisplayName("Enum 필터: PUBLISHED 상태 책만 반환된다")
    fun findByStatus_Published_ReturnsOnlyPublished() {
        val result: List<BookWithStatus> = repository.findByStatus(BookStatus.PUBLISHED)

        assertThat(result).isNotEmpty()
        assertThat(result).allMatch { it.status == BookStatus.PUBLISHED }

        val hasPublished = result.any { it.title == "TypeBook Published" }
        assertThat(hasPublished).isTrue()
    }

    @Test
    @DisplayName("Enum 필터: DRAFT 상태 책만 반환된다")
    fun findByStatus_Draft_ReturnsOnlyDraft() {
        val result: List<BookWithStatus> = repository.findByStatus(BookStatus.DRAFT)

        assertThat(result).isNotEmpty()
        assertThat(result).allMatch { it.status == BookStatus.DRAFT }
    }

    @Test
    @DisplayName("전체 조회: TypeBook 시리즈 status가 null이 아니다")
    fun findAllWithStatus_NoNullStatus() {
        val result: List<BookWithStatus> = repository.findAllWithStatus()

        assertThat(result).isNotEmpty()
        result.filter { it.title?.startsWith("TypeBook") == true }
              .forEach { assertThat(it.status).isNotNull() }
    }

    @Test
    @DisplayName("Enum 업데이트: 상태 변경 후 재조회 시 변경된 값이 반환된다")
    fun updateBookStatus_ChangesStatus() {
        val updated = repository.updateBookStatus(502, BookStatus.ARCHIVED)
        assertThat(updated).isEqualTo(1)

        val archivedBooks = repository.findByStatus(BookStatus.ARCHIVED)
        val hasDraftNowArchived = archivedBooks.any { it.id == 502 }
        assertThat(hasDraftNowArchived).isTrue()
    }

    @Test
    @DisplayName("Enum 업데이트: 반환값이 1이다 (1건 변경)")
    fun updateBookStatus_ReturnsAffectedRowCount() {
        val affected = repository.updateBookStatus(501, BookStatus.DRAFT)
        assertThat(affected).isEqualTo(1)
    }
}
