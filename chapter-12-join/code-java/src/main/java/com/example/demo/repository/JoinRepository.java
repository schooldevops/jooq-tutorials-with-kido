package com.example.demo.repository;

import com.example.demo.dto.BookWithAuthor;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.jooq.Tables.AUTHOR;
import static com.example.jooq.Tables.BOOK;

/**
 * 【Chapter 12】 다중 테이블 조인 (JOIN)
 * INNER JOIN, LEFT JOIN, WHERE 조건 포함 JOIN을 실습합니다.
 */
@Repository
@RequiredArgsConstructor
public class JoinRepository {

    private final DSLContext dsl;

    /**
     * INNER JOIN: author_id가 매핑된(저자가 있는) 책만 반환합니다.
     * author_id가 NULL인 책은 결과에 포함되지 않습니다.
     */
    public List<BookWithAuthor> findBooksWithAuthor() {
        return dsl.select(
                        BOOK.ID,
                        BOOK.TITLE,
                        BOOK.PUBLISHED_YEAR,
                        AUTHOR.FIRST_NAME,
                        AUTHOR.LAST_NAME
                )
                .from(BOOK)
                .join(AUTHOR).on(BOOK.AUTHOR_ID.eq(AUTHOR.ID))
                .orderBy(BOOK.TITLE.asc())
                .fetchInto(BookWithAuthor.class);
    }

    /**
     * LEFT JOIN: 저자가 없는 책도 포함하여 반환합니다.
     * 저자가 없는 경우 firstName, lastName은 null이 됩니다.
     */
    public List<BookWithAuthor> findAllBooksWithAuthor() {
        return dsl.select(
                        BOOK.ID,
                        BOOK.TITLE,
                        BOOK.PUBLISHED_YEAR,
                        AUTHOR.FIRST_NAME,
                        AUTHOR.LAST_NAME
                )
                .from(BOOK)
                .leftJoin(AUTHOR).on(BOOK.AUTHOR_ID.eq(AUTHOR.ID))
                .orderBy(BOOK.TITLE.asc())
                .fetchInto(BookWithAuthor.class);
    }

    /**
     * INNER JOIN + WHERE: 특정 연도 이후의 책만 저자 정보와 함께 반환합니다.
     */
    public List<BookWithAuthor> findBooksAfterYearWithAuthor(int year) {
        return dsl.select(
                        BOOK.ID,
                        BOOK.TITLE,
                        BOOK.PUBLISHED_YEAR,
                        AUTHOR.FIRST_NAME,
                        AUTHOR.LAST_NAME
                )
                .from(BOOK)
                .join(AUTHOR).on(BOOK.AUTHOR_ID.eq(AUTHOR.ID))
                .where(BOOK.PUBLISHED_YEAR.ge(year))
                .orderBy(BOOK.PUBLISHED_YEAR.asc())
                .fetchInto(BookWithAuthor.class);
    }
}
