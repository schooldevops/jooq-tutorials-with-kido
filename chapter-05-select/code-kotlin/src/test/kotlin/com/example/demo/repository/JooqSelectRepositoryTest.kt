package com.example.demo.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class JooqSelectRepositoryTest {

    @Autowired
    private lateinit var repository: JooqSelectRepository

    @Test
    @DisplayName("DB에서 전체 작가 레코드를 정상적으로 조회한다")
    fun findAllAuthorsTest() {
        // when
        val authors = repository.findAllAuthors()

        // then
        assertThat(authors).isNotNull
    }

    @Test
    @DisplayName("조건(이름, ID)에 맞는 작가를 Data Class 리스트로 매핑하여 반환한다")
    fun findAuthorsByLastNameAndIdTest() {
        // when
        val authors = repository.findAuthorsByLastNameAndId("S", 0)

        // then
        assertThat(authors).isNotNull
        
        // 반환된 작가들의 ID가 모두 0 초과인지 검증합니다.
        assertThat(authors).allSatisfy { a ->
            assertThat(a.id).isGreaterThan(0)
        }
    }
}
