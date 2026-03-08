package com.example.demo.repository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.BookWithStatus;
import com.example.demo.type.BookStatus;

@SpringBootTest
@Transactional
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class CustomTypeRepositoryTest {

    @Autowired
    private CustomTypeRepository repository;

    @Test
    @DisplayName("Enum 필터: PUBLISHED 상태 책만 반환된다")
    void findByStatus_Published_ReturnsOnlyPublished() {
        List<BookWithStatus> result = repository.findByStatus(BookStatus.PUBLISHED);

        // TypeBook Published, TypeBook Published2 포함
        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(b -> b.status() == BookStatus.PUBLISHED);

        boolean hasPublished = result.stream().anyMatch(b -> "TypeBook Published".equals(b.title()));
        assertThat(hasPublished).isTrue();
    }

    @Test
    @DisplayName("Enum 필터: DRAFT 상태 책만 반환된다")
    void findByStatus_Draft_ReturnsOnlyDraft() {
        List<BookWithStatus> result = repository.findByStatus(BookStatus.DRAFT);

        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(b -> b.status() == BookStatus.DRAFT);
    }

    @Test
    @DisplayName("전체 조회: 모든 결과의 status가 null이 아니다")
    void findAllWithStatus_NoNullStatus() {
        List<BookWithStatus> result = repository.findAllWithStatus();

        assertThat(result).isNotEmpty();
        // TypeTest 책들은 모두 status가 있음
        result.stream()
              .filter(b -> b.title() != null && b.title().startsWith("TypeBook"))
              .forEach(b -> assertThat(b.status()).isNotNull());
    }

    @Test
    @DisplayName("Enum 업데이트: 상태 변경 후 변경된 값이 조회된다")
    void updateBookStatus_ChangesStatus() {
        // when: TypeBook Draft를 ARCHIVED로 변경
        int updated = repository.updateBookStatus(502, BookStatus.ARCHIVED);
        assertThat(updated).isEqualTo(1);

        // then: ARCHIVED로 재조회 시 포함됨
        List<BookWithStatus> archivedBooks = repository.findByStatus(BookStatus.ARCHIVED);
        boolean hasDraftNowArchived = archivedBooks.stream()
                .anyMatch(b -> b.id() != null && b.id() == 502);
        assertThat(hasDraftNowArchived).isTrue();
    }

    @Test
    @DisplayName("Enum 업데이트: 반환값이 1이다 (1건 변경)")
    void updateBookStatus_ReturnsAffectedRowCount() {
        int affected = repository.updateBookStatus(501, BookStatus.DRAFT);
        assertThat(affected).isEqualTo(1);
    }
}
