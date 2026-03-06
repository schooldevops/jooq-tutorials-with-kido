package com.example.demo.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional // 각 테스트 종료 후 자동 롤백으로 DB 클린 유지
class JooqInsertRepositoryTest {

    @Autowired
    private lateinit var repository: JooqInsertRepository

    @Test
    @DisplayName("DSL 스타일로 작가를 삽입하면 영향받은 행 수가 1이다")
    fun insertAuthorDslTest() {
        // given
        val firstName = "Leo"
        val lastName = "Tolstoy"

        // when
        val affectedRows = repository.insertAuthorDsl(firstName, lastName)

        // then
        assertThat(affectedRows).isEqualTo(1)
    }

    @Test
    @DisplayName("UpdatableRecord 방식으로 작가를 삽입하면 DB generated ID가 채워진 레코드를 반환한다")
    fun insertAuthorWithRecordTest() {
        // given
        val firstName = "Franz"
        val lastName = "Kafka"

        // when
        val record = repository.insertAuthorWithRecord(firstName, lastName)

        // then
        assertThat(record).isNotNull
        assertThat(record.id).isNotNull()      // SERIAL로 생성된 PK
        assertThat(record.id).isGreaterThan(0)
        assertThat(record.firstName).isEqualTo(firstName)
        assertThat(record.lastName).isEqualTo(lastName)
    }

    @Test
    @DisplayName("INSERT ... RETURNING으로 책을 삽입하면 DB가 생성한 PK를 즉시 반환한다")
    fun insertBookReturningIdTest() {
        // given: 먼저 테스트용 작가를 삽입하고 생성된 ID를 얻음
        val author = repository.insertAuthorWithRecord("Victor", "Hugo")
        val authorId = author.id!!

        // when
        val newBookId = repository.insertBookReturningId("Les Misérables", authorId, 1862)

        // then
        assertThat(newBookId).isNotNull()
        assertThat(newBookId).isGreaterThan(0)
    }
}
