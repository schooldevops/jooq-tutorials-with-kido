package com.example.demo.repository;

import com.example.jooq.tables.pojos.Book;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.jooq.Tables.BOOK;

@Repository
@RequiredArgsConstructor
public class JooqSortingPagingRepository {

    private final DSLContext dsl;

    /**
     * 【단일 컬럼 오름차순 정렬】 책 목록을 제목 오름차순으로 정렬합니다.
     * SQL: SELECT * FROM book ORDER BY title ASC
     */
    public List<Book> findBooksOrderedByTitle() {
        return dsl.selectFrom(BOOK)
                  .orderBy(BOOK.TITLE.asc())
                  .fetchInto(Book.class);
    }

    /**
     * 【내림차순 정렬】 출판연도 내림차순으로 정렬합니다 (최신순).
     * SQL: SELECT * FROM book ORDER BY published_year DESC
     */
    public List<Book> findBooksOrderedByYearDesc() {
        return dsl.selectFrom(BOOK)
                  .orderBy(BOOK.PUBLISHED_YEAR.desc())
                  .fetchInto(Book.class);
    }

    /**
     * 【Offset 페이징】 page(0-based)와 size로 슬라이싱합니다.
     * SQL: SELECT * FROM book ORDER BY title ASC LIMIT {size} OFFSET {page*size}
     */
    public List<Book> findBooksWithPaging(int page, int size) {
        return dsl.selectFrom(BOOK)
                  .orderBy(BOOK.TITLE.asc())
                  .limit(size)
                  .offset((long) page * size)
                  .fetchInto(Book.class);
    }

    /**
     * 【다중 정렬 + 페이징】 출판연도 내림차순 → 제목 오름차순 정렬 후 페이징 적용.
     * SQL: SELECT * FROM book ORDER BY published_year DESC, title ASC LIMIT {size} OFFSET {page*size}
     */
    public List<Book> findBooksWithMultiSort(int page, int size) {
        return dsl.selectFrom(BOOK)
                  .orderBy(
                      BOOK.PUBLISHED_YEAR.desc(),
                      BOOK.TITLE.asc()
                  )
                  .limit(size)
                  .offset((long) page * size)
                  .fetchInto(Book.class);
    }
}
