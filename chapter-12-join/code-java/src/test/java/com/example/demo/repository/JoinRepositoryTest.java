package com.example.demo.repository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.BookWithAuthor;

@SpringBootTest
@Transactional
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class JoinRepositoryTest {

    @Autowired
    private JoinRepository repository;

    @Test
    @DisplayName("INNER JOIN: 저자가 있는 책만 반환된다")
    void findBooksWithAuthor_InnerJoin() {
        // when
        List<BookWithAuthor> books = repository.findBooksWithAuthor();

        // then: JoinBook Alpha, Beta, Gamma (저자 있는 3건) + 기존 3건 = 6건 이상
        // JoinBook Delta(author=NULL)은 INNER JOIN에서 제외됨
        boolean hasJoinTestBooks = books.stream()
                .anyMatch(b -> b.title() != null && b.title().startsWith("JoinBook"));
        assertThat(hasJoinTestBooks).isTrue();

        // INNER JOIN이므로 firstName이 null인 행이 없어야 함
        boolean hasNullFirstName = books.stream()
                .filter(b -> b.title() != null && b.title().startsWith("JoinBook"))
                .anyMatch(b -> b.firstName() == null);
        assertThat(hasNullFirstName).isFalse();
    }

    @Test
    @DisplayName("INNER JOIN: 저자 없는 책(JoinBook Delta)은 결과에서 제외된다")
    void findBooksWithAuthor_ExcludesNullAuthor() {
        // when
        List<BookWithAuthor> books = repository.findBooksWithAuthor();

        // then: JoinBook Delta는 author_id=NULL이므로 제외되어야 함
        boolean hasDelta = books.stream()
                .anyMatch(b -> "JoinBook Delta".equals(b.title()));
        assertThat(hasDelta).isFalse();
    }

    @Test
    @DisplayName("LEFT JOIN: 저자 없는 책(JoinBook Delta)도 결과에 포함된다")
    void findAllBooksWithAuthor_LeftJoin_IncludesNullAuthor() {
        // when
        List<BookWithAuthor> books = repository.findAllBooksWithAuthor();

        // then: JoinBook Delta 포함 여부 확인
        boolean hasDelta = books.stream()
                .anyMatch(b -> "JoinBook Delta".equals(b.title()));
        assertThat(hasDelta).isTrue();

        // JoinBook Delta의 firstName은 null이어야 함
        books.stream()
                .filter(b -> "JoinBook Delta".equals(b.title()))
                .findFirst()
                .ifPresent(b -> assertThat(b.firstName()).isNull());
    }

    @Test
    @DisplayName("LEFT JOIN 결과는 INNER JOIN 결과보다 같거나 많다")
    void leftJoin_CountGreaterThanOrEqualToInnerJoin() {
        // when
        List<BookWithAuthor> innerJoinBooks = repository.findBooksWithAuthor();
        List<BookWithAuthor> leftJoinBooks = repository.findAllBooksWithAuthor();

        // then
        assertThat(leftJoinBooks.size()).isGreaterThanOrEqualTo(innerJoinBooks.size());
    }

    @Test
    @DisplayName("INNER JOIN + WHERE: 특정 연도 이후 책만 저자와 함께 반환된다")
    void findBooksAfterYearWithAuthor_FilteredByYear() {
        // when: 2010년 이후 책 조회
        List<BookWithAuthor> books = repository.findBooksAfterYearWithAuthor(2010);

        // then: JoinBook Beta(2010), JoinBook Gamma(2020) 포함되어야 함
        boolean hasBeta = books.stream().anyMatch(b -> "JoinBook Beta".equals(b.title()));
        boolean hasGamma = books.stream().anyMatch(b -> "JoinBook Gamma".equals(b.title()));
        boolean hasAlpha = books.stream().anyMatch(b -> "JoinBook Alpha".equals(b.title()));

        assertThat(hasBeta).isTrue();
        assertThat(hasGamma).isTrue();
        assertThat(hasAlpha).isFalse(); // 2000년 → 제외

        // 모든 결과의 publishedYear >= 2010 검증
        books.stream()
                .filter(b -> b.title() != null && b.title().startsWith("JoinBook"))
                .forEach(b -> assertThat(b.publishedYear()).isGreaterThanOrEqualTo(2010));
    }
}
