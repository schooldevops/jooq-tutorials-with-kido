package com.example.demo.repository;

import com.example.demo.dto.BookSummaryDto;
import com.example.jooq.tables.pojos.Book;
import com.example.jooq.tables.records.BookRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.example.jooq.Tables.AUTHOR;
import static com.example.jooq.Tables.BOOK;

@Repository
@RequiredArgsConstructor
public class JooqRecordPojoRepository {

    private final DSLContext dsl;

    /**
     * 【Record 타입】 BOOK 전체를 jOOQ 자동 생성 Record 타입으로 반환합니다.
     * Record는 UpdatableRecord를 상속하므로 store(), delete() 등 활성 메서드 사용 가능.
     */
    public List<BookRecord> fetchAllAsRecord() {
        return dsl.selectFrom(BOOK)
                  .fetch();
    }

    /**
     * 【POJO 매핑】 BOOK 전체를 자동 생성 POJO(Book)로 변환하여 반환합니다.
     * fetchInto()는 컬럼명을 camelCase로 자동 매핑합니다.
     */
    public List<Book> fetchAllAsPojo() {
        return dsl.selectFrom(BOOK)
                  .fetchInto(Book.class);
    }

    /**
     * 【Map 변환】 BOOK.ID → BOOK.TITLE 형태의 Key-Value Map을 반환합니다.
     * ID로 제목을 빠르게 조회하는 룩업 테이블로 활용합니다.
     */
    public Map<Integer, String> fetchAsMap() {
        return dsl.select(BOOK.ID, BOOK.TITLE)
                  .from(BOOK)
                  .fetchMap(BOOK.ID, BOOK.TITLE);
    }

    /**
     * 【커스텀 DTO 매핑】 AUTHOR와 BOOK을 JOIN한 뒤 람다로 BookSummaryDto로 직접 조립합니다.
     * 여러 테이블의 컬럼을 조합해야 할 때 가장 유연한 방식입니다.
     */
    public List<BookSummaryDto> fetchIntoCustomDto() {
        return dsl.select(BOOK.ID, BOOK.TITLE, AUTHOR.LAST_NAME)
                  .from(BOOK)
                  .join(AUTHOR).on(BOOK.AUTHOR_ID.eq(AUTHOR.ID))
                  .fetch(r -> new BookSummaryDto(
                      r.get(BOOK.ID),
                      r.get(BOOK.TITLE),
                      r.get(AUTHOR.LAST_NAME)
                  ));
    }
}
