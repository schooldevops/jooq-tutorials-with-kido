package com.example.demo.repository;

import com.example.demo.dto.AuthorBookCount;
import com.example.demo.dto.RecentBook;
import com.example.jooq.tables.pojos.Book;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

import static com.example.jooq.Tables.AUTHOR;
import static com.example.jooq.Tables.BOOK;

/**
 * 【Chapter 13】 서브쿼리와 CTE
 * - Scalar Subquery: WHERE 절에서 단일 값 서브쿼리 활용
 * - CTE (with()): 복잡한 집계를 이름 붙인 임시 결과셋으로 분리
 * - CTE + JOIN: CTE 결과와 메인 쿼리 JOIN
 */
@Repository
@RequiredArgsConstructor
public class SubqueryCteRepository {

    private final DSLContext dsl;

    /**
     * Scalar Subquery: 전체 평균 출판연도보다 이후에 출판된 책만 조회
     * 서브쿼리가 WHERE 조건에서 단일 값(평균)을 반환합니다.
     */
    public List<Book> findBooksAboveAvgYear() {
        // 서브쿼리: 전체 book의 평균 published_year를 반환하는 SELECT
        var avgYearSubquery = DSL.select(DSL.avg(BOOK.PUBLISHED_YEAR)).from(BOOK);

        return dsl.selectFrom(BOOK)
                  .where(BOOK.PUBLISHED_YEAR.gt(
                      DSL.coerce(avgYearSubquery.asField(), Integer.class)
                  ))
                  .orderBy(BOOK.PUBLISHED_YEAR.desc())
                  .fetchInto(Book.class);
    }

    /**
     * CTE: 저자별 책 수를 집계한 뒤 AuthorBookCount 목록 반환
     * WITH book_count AS (SELECT author_id, count(*) as cnt FROM book GROUP BY author_id)
     * SELECT a.id, a.first_name, a.last_name, bc.cnt
     * FROM book_count bc JOIN author a ON a.id = bc.author_id
     */
    public List<AuthorBookCount> findAuthorsWithBookCount() {
        var bookCountCte = DSL.name("book_count")
                .fields("author_id", "cnt")
                .as(DSL.select(BOOK.AUTHOR_ID, DSL.count().as("cnt"))
                        .from(BOOK)
                        .groupBy(BOOK.AUTHOR_ID));

        return dsl.with(bookCountCte)
                  .select(
                      AUTHOR.ID,
                      AUTHOR.FIRST_NAME,
                      AUTHOR.LAST_NAME,
                      bookCountCte.field("cnt")
                  )
                  .from(bookCountCte)
                  .join(AUTHOR).on(AUTHOR.ID.eq(bookCountCte.field("author_id", Integer.class)))
                  .orderBy(bookCountCte.field("cnt", Integer.class).desc())
                  .fetchInto(AuthorBookCount.class);
    }

    /**
     * CTE + JOIN: 저자별 최신 출판연도를 CTE로 구한 뒤,
     * 해당 연도의 책 상세 정보를 BOOK + AUTHOR JOIN으로 조회
     */
    public List<RecentBook> findRecentBooksPerAuthor() {
        var maxYearCte = DSL.name("max_year")
                .fields("author_id", "max_y")
                .as(DSL.select(BOOK.AUTHOR_ID, DSL.max(BOOK.PUBLISHED_YEAR).as("max_y"))
                        .from(BOOK)
                        .groupBy(BOOK.AUTHOR_ID));

        return dsl.with(maxYearCte)
                  .select(
                      BOOK.ID, BOOK.TITLE, BOOK.AUTHOR_ID, BOOK.PUBLISHED_YEAR,
                      AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME
                  )
                  .from(BOOK)
                  .join(AUTHOR).on(BOOK.AUTHOR_ID.eq(AUTHOR.ID))
                  .join(maxYearCte)
                      .on(BOOK.AUTHOR_ID.eq(maxYearCte.field("author_id", Integer.class))
                          .and(BOOK.PUBLISHED_YEAR.eq(maxYearCte.field("max_y", Integer.class))))
                  .orderBy(AUTHOR.LAST_NAME.asc(), BOOK.TITLE.asc())
                  .fetchInto(RecentBook.class);
    }
}
