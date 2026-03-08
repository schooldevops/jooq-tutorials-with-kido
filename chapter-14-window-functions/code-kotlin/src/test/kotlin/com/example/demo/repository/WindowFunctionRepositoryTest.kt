package com.example.demo.repository

import com.example.demo.dto.BookRank
import com.example.demo.dto.YearlyRunningTotal
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
class WindowFunctionRepositoryTest {

    @Autowired
    private lateinit var repository: WindowFunctionRepository

    @Test
    @DisplayName("RANK: 모든 결과의 rank_in_author >= 1")
    fun rankBooks_AllRanksAtLeastOne() {
        val result: List<BookRank> = repository.rankBooksByYearPerAuthor()
        assertThat(result).isNotEmpty()
        assertThat(result).allMatch { (it.rankInAuthor ?: 0) >= 1 }
    }

    @Test
    @DisplayName("RANK: WinBook Gamma(2020)는 AuthorP 내 RANK 1")
    fun rankBooks_LatestBookIsRankOne() {
        val result: List<BookRank> = repository.rankBooksByYearPerAuthor()
        result.find { it.title == "WinBook Gamma" }
              ?.let { assertThat(it.rankInAuthor).isEqualTo(1) }
    }

    @Test
    @DisplayName("SUM OVER: running_total은 단조 증가한다")
    fun runningTotal_IsMonotonicallyIncreasing() {
        val result: List<YearlyRunningTotal> = repository.runningTotalByYear()
        assertThat(result).isNotEmpty()
        for (i in 1 until result.size) {
            assertThat(result[i].runningTotal)
                .isGreaterThanOrEqualTo(result[i - 1].runningTotal)
        }
    }

    @Test
    @DisplayName("RANK+CTE: 결과 모두 rank_in_author == 1")
    fun findTopRanked_AllRankOne() {
        val result: List<BookRank> = repository.findTopRankedBookPerAuthor()
        assertThat(result).isNotEmpty()
        assertThat(result).allMatch { it.rankInAuthor == 1 }
    }

    @Test
    @DisplayName("RANK+CTE: AuthorP의 1위는 WinBook Gamma(2020)")
    fun findTopRanked_AuthorPTopIsGamma() {
        val result: List<BookRank> = repository.findTopRankedBookPerAuthor()
        val hasGamma = result.any {
            it.title == "WinBook Gamma" && it.authorId == 401 && it.rankInAuthor == 1
        }
        assertThat(hasGamma).isTrue()
    }
}
