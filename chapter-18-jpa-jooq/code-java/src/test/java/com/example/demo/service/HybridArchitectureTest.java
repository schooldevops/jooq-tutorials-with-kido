package com.example.demo.service;

import com.example.demo.repository.AuthorJpaRepository;
import com.example.jooq.tables.pojos.Author;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class HybridArchitectureTest {

    @Autowired
    private AuthorService authorService;

    @Autowired
    private AuthorJpaRepository jpaRepository;

    @AfterEach
    void tearDown() {
        jpaRepository.deleteById(7001); // 7001번 ID 정리
        jpaRepository.deleteById(7002); // 7002번 ID 정리
    }

    @Test
    @DisplayName("CQRS 실패작: Flush 없이 jOOQ 조회하면 트랜잭션 진행 중임에도 못 찾는다")
    void testOmittedFlush() {
        int id = 7001;

        Optional<Author> authorOpt = authorService.saveWithJpaAndReadOmittedFlush(id);

        // jOOQ 조회 시도 실패 (객체가 비어있음을 보장)
        assertThat(authorOpt).isEmpty();
        
        // 메서드가 정상 종료되며 이때 Transaction이 Commit 됨. (그 직전에 Flush 발생)
    }

    @Test
    @DisplayName("CQRS 성공작: Flush 후엔 트랜잭션 내에서 jOOQ로 안전하게 조회된다")
    void testWithFlush() {
        int id = 7002;

        Optional<Author> authorOpt = authorService.saveWithJpaAndReadWithFlush(id);

        // jOOQ도 DB를 찌르기 때문에 Flush된 데이터를 정상 조회 가능
        assertThat(authorOpt).isPresent();
        assertThat(authorOpt.get().getFirstName()).isEqualTo("Flushed");
    }
}
