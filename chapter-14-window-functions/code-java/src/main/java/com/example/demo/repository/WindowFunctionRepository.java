package com.example.demo.repository;

import com.example.demo.dto.BookRank;
import com.example.demo.dto.YearlyRunningTotal;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.jooq.Tables.BOOK;

/**
 * 【Chapter 14】 윈도우 함수 및 분석용 SQL
 * - RANK() OVER (PARTITION BY ... ORDER BY ...)
 * - SUM() OVER (ORDER BY ...) - 누적합
 * - RANK() + CTE - 1위 필터링
 */
@Repository
@RequiredArgsConstructor
public class WindowFunctionRepository {

    private final DSLContext dsl;

    /**
     * RANK() OVER (PARTITION BY author_id ORDER BY published_year DESC)
     * 저자별 출판연도 기준 내림차순 순위를 계산합니다.
     */
    public List<BookRank> rankBooksByYearPerAuthor() {
        var rankField = DSL.rank()
                .over(DSL.partitionBy(BOOK.AUTHOR_ID)
                          .orderBy(BOOK.PUBLISHED_YEAR.desc()))
                .as("rank_in_author");

        return dsl.select(
                        BOOK.ID,
                        BOOK.TITLE,
                        BOOK.AUTHOR_ID,
                        BOOK.PUBLISHED_YEAR,
                        rankField
                )
                .from(BOOK)
                .orderBy(BOOK.AUTHOR_ID.asc(), DSL.field("rank_in_author").asc())
                .fetchInto(BookRank.class);
    }

    /**
     * SUM(COUNT) OVER (ORDER BY published_year) - 연도별 누적 출판 수
     */
    public List<YearlyRunningTotal> runningTotalByYear() {
        var bookCount = DSL.count().as("book_count");
        var runningTotal = DSL.sum(DSL.count())
                               .over(DSL.orderBy(BOOK.PUBLISHED_YEAR.asc()))
                               .as("running_total");

        return dsl.select(BOOK.PUBLISHED_YEAR, bookCount, runningTotal)
                  .from(BOOK)
                  .groupBy(BOOK.PUBLISHED_YEAR)
                  .orderBy(BOOK.PUBLISHED_YEAR.asc())
                  .fetchInto(YearlyRunningTotal.class);
    }

    /**
     * RANK() + CTE: 저자별 1위(최신) 책만 추출
     * CTE로 RANK를 계산한 후 rank_in_author = 1 인 행만 필터링합니다.
     */
    public List<BookRank> findTopRankedBookPerAuthor() {
        var rankField = DSL.rank()
                .over(DSL.partitionBy(BOOK.AUTHOR_ID)
                          .orderBy(BOOK.PUBLISHED_YEAR.desc()))
                .as("rank_in_author");

        var rankedCte = DSL.name("ranked")
                .fields("id", "title", "author_id", "published_year", "rank_in_author")
                .as(DSL.select(BOOK.ID, BOOK.TITLE, BOOK.AUTHOR_ID, BOOK.PUBLISHED_YEAR, rankField)
                        .from(BOOK));

        return dsl.with(rankedCte)
                  .selectFrom(rankedCte)
                  .where(rankedCte.field("rank_in_author", Integer.class).eq(1))
                  .orderBy(rankedCte.field("author_id"))
                  .fetchInto(BookRank.class);
    }
}
