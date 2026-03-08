package com.example.demo.repository;

import com.example.demo.dto.AuthorBookCount;
import com.example.demo.dto.RecentBook;
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
class SubqueryCteRepositoryTest {

    @Autowired
    private SubqueryCteRepository repository;

    @Test
    @DisplayName("Scalar Subquery: 평균 출판연도보다 이후 책만 반환된다")
    void findBooksAboveAvgYear_ReturnsOnlyAboveAverage() {
        // when
        List<Book> books = repository.findBooksAboveAvgYear();

        // then: CteBook B(2015), CteBook C(2018), CteBook D(2022) 포함될 가능성
        // 전체 published_year 평균 기준으로 필터됨 (기본 데이터 포함)
        assertThat(books).isNotEmpty();
        // 결과의 모든 책은 최신순 정렬
        for (int i = 0; i < books.size() - 1; i++) {
            assertThat(books.get(i).getPublishedYear())
                    .isGreaterThanOrEqualTo(books.get(i + 1).getPublishedYear());
        }
    }

    @Test
    @DisplayName("Scalar Subquery: CteBook D(2022)는 결과에 반드시 포함된다")
    void findBooksAboveAvgYear_IncludesLatestBook() {
        // when
        List<Book> books = repository.findBooksAboveAvgYear();

        // then: 2022년도 CteBook D는 어떤 평균이든 포함되어야 함
        boolean hasLatest = books.stream()
                .anyMatch(b -> "CteBook D".equals(b.getTitle()));
        assertThat(hasLatest).isTrue();
    }

    @Test
    @DisplayName("CTE: 저자별 책 수가 올바르게 집계된다")
    void findAuthorsWithBookCount_CountsCorrectly() {
        // when
        List<AuthorBookCount> result = repository.findAuthorsWithBookCount();

        // then: 집계 결과의 cnt >= 1
        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(a -> a.cnt() >= 1);

        // CteTest 저자들 각각 2권씩
        result.stream()
              .filter(a -> "CteTest".equals(a.firstName()))
              .forEach(a -> assertThat(a.cnt()).isEqualTo(2));
    }

    @Test
    @DisplayName("CTE + JOIN: 저자별 최신 책만 반환된다")
    void findRecentBooksPerAuthor_ReturnsLatestPerAuthor() {
        // when
        List<RecentBook> result = repository.findRecentBooksPerAuthor();

        // then: CteTest AuthorX의 최신 = CteBook B(2015)
        boolean hasAuthorXLatest = result.stream()
                .anyMatch(b -> "CteBook B".equals(b.title()) && b.publishedYear() == 2015);
        assertThat(hasAuthorXLatest).isTrue();

        // then: CteTest AuthorY의 최신 = CteBook D(2022)
        boolean hasAuthorYLatest = result.stream()
                .anyMatch(b -> "CteBook D".equals(b.title()) && b.publishedYear() == 2022);
        assertThat(hasAuthorYLatest).isTrue();
    }

    @Test
    @DisplayName("CTE + JOIN: 저자 이름도 함께 조회된다")
    void findRecentBooksPerAuthor_IncludesAuthorName() {
        // when
        List<RecentBook> result = repository.findRecentBooksPerAuthor();

        // then: CteTest 저자의 책 결과에 firstName 값 있음
        result.stream()
              .filter(b -> b.title() != null && b.title().startsWith("CteBook"))
              .forEach(b -> assertThat(b.firstName()).isNotNull());
    }
}
