package com.example.demo.service

import com.example.demo.repository.TransactionRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TransactionTest {

    @Autowired
    private lateinit var service: TransactionService

    @Autowired
    private lateinit var repository: TransactionRepository

    @Test
    @DisplayName("커밋 검증: 예외 없이 종료되면 jOOQ 삽입 데이터가 커밋된다")
    fun commit_SavesDataSuccessfully() {
        val authorId = 603

        service.saveAuthorAndBookSuccessfully(authorId)

        val name = repository.findAuthorNameById(authorId)
        assertThat(name).isEqualTo("Tx")
    }

    @Test
    @DisplayName("롤백 검증: RuntimeException 발생 시 jOOQ 트랜잭션도 함께 롤백된다")
    fun rollback_OnRuntimeException() {
        val authorId = 604

        assertThrows<RuntimeException> {
            service.saveAuthorAndThrowException(authorId)
        }

        // 롤백되었으므로 데이터가 없어야 함
        val name = repository.findAuthorNameById(authorId)
        assertThat(name).isNull()
    }
}
