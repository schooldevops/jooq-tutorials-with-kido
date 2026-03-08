package com.example.demo.repository;

import com.example.demo.converter.BookStatusConverter;
import com.example.demo.dto.BookWithStatus;
import com.example.demo.type.BookStatus;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.jooq.Tables.BOOK;

/**
 * 【Chapter 15】 커스텀 데이터 타입과 컨버터
 * - BookStatus Enum ↔ DB VARCHAR 변환
 * - status 컬럼은 런타임에 추가되므로 DSL.field()로 동적 참조
 */
@Repository
@RequiredArgsConstructor
public class CustomTypeRepository {

    private final DSLContext dsl;
    private final BookStatusConverter converter = new BookStatusConverter();

    // status 컬럼 - 코드 생성에 없으므로 DSL.field()로 참조
    private static final Field<String> STATUS = DSL.field("status", String.class);

    /**
     * Enum 기반 필터 조회: WHERE status = 'PUBLISHED'
     */
    public List<BookWithStatus> findByStatus(BookStatus status) {
        return dsl.select(BOOK.ID, BOOK.TITLE, BOOK.PUBLISHED_YEAR, STATUS)
                  .from(BOOK)
                  .where(STATUS.eq(converter.to(status)))
                  .orderBy(BOOK.TITLE.asc())
                  .fetch(r -> new BookWithStatus(
                          r.get(BOOK.ID),
                          r.get(BOOK.TITLE),
                          r.get(BOOK.PUBLISHED_YEAR),
                          converter.from(r.get(STATUS))
                  ));
    }

    /**
     * 전체 조회 + Enum 변환
     */
    public List<BookWithStatus> findAllWithStatus() {
        return dsl.select(BOOK.ID, BOOK.TITLE, BOOK.PUBLISHED_YEAR, STATUS)
                  .from(BOOK)
                  .orderBy(BOOK.TITLE.asc())
                  .fetch(r -> new BookWithStatus(
                          r.get(BOOK.ID),
                          r.get(BOOK.TITLE),
                          r.get(BOOK.PUBLISHED_YEAR),
                          converter.from(r.get(STATUS))
                  ));
    }

    /**
     * Enum → VARCHAR UPDATE: SET status = 'ARCHIVED'
     */
    public int updateBookStatus(int bookId, BookStatus newStatus) {
        return dsl.update(BOOK)
                  .set(STATUS, converter.to(newStatus))
                  .where(BOOK.ID.eq(bookId))
                  .execute();
    }
}
