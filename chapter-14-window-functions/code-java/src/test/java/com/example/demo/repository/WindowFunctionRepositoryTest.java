package com.example.demo.repository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.BookRank;
import com.example.demo.dto.YearlyRunningTotal;

@SpringBootTest
@Transactional
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class WindowFunctionRepositoryTest {

    @Autowired
    private WindowFunctionRepository repository;

    @Test
    @DisplayName("RANK: 모든 결과의 rank_in_author >= 1")
    void rankBooks_AllRanksAtLeastOne() {
        List<BookRank> result = repository.rankBooksByYearPerAuthor();

        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(b -> b.rankInAuthor() >= 1);
    }

    @Test
    @DisplayName("RANK: WinBook Gamma(2020)는 AuthorP 내 RANK 1")
    void rankBooks_LatestBookIsRankOne() {
        List<BookRank> result = repository.rankBooksByYearPerAuthor();

        result.stream()
              .filter(b -> "WinBook Gamma".equals(b.title()))
              .findFirst()
              .ifPresent(b -> assertThat(b.rankInAuthor()).isEqualTo(1));
    }

    @Test
    @DisplayName("SUM OVER: running_total은 단조 증가한다")
    void runningTotal_IsMonotonicallyIncreasing() {
        List<YearlyRunningTotal> result = repository.runningTotalByYear();

        assertThat(result).isNotEmpty();
        for (int i = 1; i < result.size(); i++) {
            assertThat(result.get(i).runningTotal())
                    .isGreaterThanOrEqualTo(result.get(i - 1).runningTotal());
        }
    }

    @Test
    @DisplayName("RANK+CTE: 결과 모두 rank_in_author == 1")
    void findTopRanked_AllRankOne() {
        List<BookRank> result = repository.findTopRankedBookPerAuthor();

        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(b -> b.rankInAuthor() == 1);
    }

    @Test
    @DisplayName("RANK+CTE: AuthorP의 1위는 WinBook Gamma(2020)")
    void findTopRanked_AuthorPTopIsGamma() {
        List<BookRank> result = repository.findTopRankedBookPerAuthor();

        boolean hasGamma = result.stream()
                .anyMatch(b -> "WinBook Gamma".equals(b.title())
                            && b.authorId() == 401
                            && b.rankInAuthor() == 1);
        assertThat(hasGamma).isTrue();
    }
}
