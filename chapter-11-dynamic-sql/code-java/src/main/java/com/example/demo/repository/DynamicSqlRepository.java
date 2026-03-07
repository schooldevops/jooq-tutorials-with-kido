package com.example.demo.repository;

import com.example.jooq.tables.pojos.Book;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.example.jooq.Tables.BOOK;

/**
 * 【Chapter 11】 동적 SQL의 마법
 * 런타임에 입력된 파라미터만 판단하여 WHERE 조건(Condition)을 조립합니다.
 */
@Repository
@RequiredArgsConstructor
public class DynamicSqlRepository {

    private final DSLContext dsl;

    /**
     * 패턴 1: Condition 리스트 조립
     * null이 아닌 파라미터만 리스트에 추가하고, 마지막에 DSL.and()로 묶습니다.
     * 빈 리스트면 DSL.and()는 항상 참(TRUE)이 되어 전체를 조회합니다.
     */
    public List<Book> searchBooks(String title, Integer authorId, Integer year) {
        List<Condition> conditions = new ArrayList<>();

        if (title != null && !title.isBlank()) {
            conditions.add(BOOK.TITLE.containsIgnoreCase(title)); // ILIKE '%title%'
        }
        if (authorId != null) {
            conditions.add(BOOK.AUTHOR_ID.eq(authorId));
        }
        if (year != null) {
            conditions.add(BOOK.PUBLISHED_YEAR.eq(year));
        }

        return dsl.selectFrom(BOOK)
                  .where(DSL.and(conditions))
                  .orderBy(BOOK.TITLE.asc())
                  .fetchInto(Book.class);
    }

    /**
     * 패턴 2: DSL.noCondition() 체이닝
     * 항상 TRUE인 빈 조건에서 시작하여 .and()를 체이닝합니다.
     */
    public List<Book> searchBooksWithNoCondition(String title, Integer year) {
        Condition condition = DSL.noCondition(); // 시작점: 1 = 1 (TRUE)

        if (title != null && !title.isBlank()) {
            condition = condition.and(BOOK.TITLE.containsIgnoreCase(title));
        }
        if (year != null) {
            condition = condition.and(BOOK.PUBLISHED_YEAR.eq(year));
        }

        return dsl.selectFrom(BOOK)
                  .where(condition)
                  .orderBy(BOOK.TITLE.asc())
                  .fetchInto(Book.class);
    }
}
