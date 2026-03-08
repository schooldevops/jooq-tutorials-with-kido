package com.example.demo.service;

import com.example.demo.repository.TransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class TransactionTest {

    @Autowired
    private TransactionService service;

    @Autowired
    private TransactionRepository repository;

    @Test
    @DisplayName("커밋 검증: 정상 종료 시 트랜잭션이 커밋되고 데이터가 저장된다")
    void commit_SavesDataSuccessfully() {
        // given
        int authorId = 601;

        // when: 예외 없이 정상 수행
        service.saveAuthorAndBookSuccessfully(authorId);

        // then: DB에 데이터가 존재해야 함
        Optional<String> name = repository.findAuthorNameById(authorId);
        assertThat(name).isPresent();
        assertThat(name.get()).isEqualTo("Tx");
    }

    @Test
    @DisplayName("롤백 검증: RuntimeException 발생 시 jOOQ 작업이 롤백되어 데이터가 저장되지 않는다")
    void rollback_OnRuntimeException() {
        // given
        int authorId = 602;

        // when: 예외が発生하는 메서드 호출
        assertThrows(RuntimeException.class, () -> {
            service.saveAuthorAndThrowException(authorId);
        });

        // then: 트랜잭션 롤백으로 인해 DB에 데이터가 없어야 함
        Optional<String> name = repository.findAuthorNameById(authorId);
        assertThat(name).isEmpty();
    }
}
