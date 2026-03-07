package com.example.demo.repository;

import com.example.demo.dto.BookSummaryDto;
import com.example.jooq.tables.pojos.Book;
import com.example.jooq.tables.records.BookRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class JooqRecordPojoRepositoryTest {

    @Autowired
    private JooqRecordPojoRepository repository;

    @Test
    @DisplayName("fetchAllAsRecord: BOOK 전체를 Record 타입으로 조회한다")
    void fetchAllAsRecordTest() {
        // when
        List<BookRecord> records = repository.fetchAllAsRecord();

        // then
        assertThat(records).isNotNull().isNotEmpty();
        assertThat(records).allSatisfy(r -> assertThat(r.getTitle()).isNotNull());
    }

    @Test
    @DisplayName("fetchAllAsPojo: BOOK 전체를 자동 생성 POJO로 매핑한다")
    void fetchAllAsPojoTest() {
        // when
        List<Book> books = repository.fetchAllAsPojo();

        // then
        assertThat(books).isNotNull().isNotEmpty();
        assertThat(books).allSatisfy(b -> assertThat(b.getTitle()).isNotNull());
    }

    @Test
    @DisplayName("fetchAsMap: BOOK.ID → BOOK.TITLE Map을 반환한다")
    void fetchAsMapTest() {
        // when
        Map<Integer, String> bookMap = repository.fetchAsMap();

        // then
        assertThat(bookMap).isNotNull().isNotEmpty();
        // init-script에 id=1, title="Hamlet"이 seeded 되어 있음
        assertThat(bookMap).containsKey(1);
        assertThat(bookMap.get(1)).isEqualTo("Hamlet");
    }

    @Test
    @DisplayName("fetchIntoCustomDto: AUTHOR JOIN BOOK 결과를 커스텀 DTO로 매핑한다")
    void fetchIntoCustomDtoTest() {
        // when
        List<BookSummaryDto> summaries = repository.fetchIntoCustomDto();

        // then
        assertThat(summaries).isNotNull().isNotEmpty();
        assertThat(summaries).allSatisfy(dto -> {
            assertThat(dto.id()).isNotNull();
            assertThat(dto.title()).isNotNull();
            assertThat(dto.authorLastName()).isNotNull();
        });
    }
}
