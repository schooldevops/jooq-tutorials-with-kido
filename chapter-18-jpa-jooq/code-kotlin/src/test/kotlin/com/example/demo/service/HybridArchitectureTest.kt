package com.example.demo.service

import com.example.demo.repository.AuthorJpaRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class HybridArchitectureTest {

    @Autowired
    private lateinit var authorService: AuthorService

    @Autowired
    private lateinit var jpaRepository: AuthorJpaRepository

    @AfterEach
    fun tearDown() {
        jpaRepository.deleteById(7003)
        jpaRepository.deleteById(7004)
    }

    @Test
    @DisplayName("CQRS 실패작: Flush 없이 jOOQ 조회하면 트랜잭션 내에서도 찾을 수 없다")
    fun testOmittedFlush() {
        val id = 7003
        val authorOpt = authorService.saveWithJpaAndReadOmittedFlush(id)

        // jOOQ에서 못 찾음 (null)
        assertThat(authorOpt).isNull()
    }

    @Test
    @DisplayName("CQRS 성공작: Flush 후엔 트랜잭션 내에서 jOOQ로 안전하게 조회된다")
    fun testWithFlush() {
        val id = 7004
        val authorOpt = authorService.saveWithJpaAndReadWithFlush(id)

        // DB에 반영되어 조회 성공
        assertThat(authorOpt).isNotNull
        assertThat(authorOpt?.firstName).isEqualTo("Flushed")
    }
}
