package com.example.demo.repository

import com.example.demo.dto.BookSummaryDto
import com.example.jooq.tables.pojos.Book
import com.example.jooq.tables.records.BookRecord
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class JooqRecordPojoRepositoryTest {

    @Autowired
    private lateinit var repository: JooqRecordPojoRepository

    @Test
    @DisplayName("fetchAllAsRecord: BOOK 전체를 Record 타입으로 조회한다")
    fun fetchAllAsRecordTest() {
        // when
        val records: List<BookRecord> = repository.fetchAllAsRecord()

        // then
        assertThat(records).isNotNull.isNotEmpty
        assertThat(records).allSatisfy { assertThat(it.title).isNotNull() }
    }

    @Test
    @DisplayName("fetchAllAsPojo: BOOK 전체를 자동 생성 POJO로 매핑한다")
    fun fetchAllAsPojoTest() {
        // when
        val books: List<Book> = repository.fetchAllAsPojo()

        // then
        assertThat(books).isNotNull.isNotEmpty
        assertThat(books).allSatisfy { assertThat(it.title).isNotNull() }
    }

    @Test
    @DisplayName("fetchAsMap: BOOK.ID → BOOK.TITLE Map을 반환한다")
    fun fetchAsMapTest() {
        // when
        val bookMap: Map<Int?, String?> = repository.fetchAsMap()

        // then
        assertThat(bookMap).isNotNull.isNotEmpty
        // init-script에 id=1, title="Hamlet" seeded
        assertThat(bookMap).containsKey(1)
        assertThat(bookMap[1]).isEqualTo("Hamlet")
    }

    @Test
    @DisplayName("fetchIntoCustomDto: AUTHOR JOIN BOOK 결과를 커스텀 DTO로 매핑한다")
    fun fetchIntoCustomDtoTest() {
        // when
        val summaries: List<BookSummaryDto> = repository.fetchIntoCustomDto()

        // then
        assertThat(summaries).isNotNull.isNotEmpty
        assertThat(summaries).allSatisfy { dto ->
            assertThat(dto.id).isNotNull()
            assertThat(dto.title).isNotNull()
            assertThat(dto.authorLastName).isNotNull()
        }
    }
}
