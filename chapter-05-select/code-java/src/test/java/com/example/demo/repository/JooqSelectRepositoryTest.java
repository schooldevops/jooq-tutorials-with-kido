package com.example.demo.repository;

import com.example.jooq.tables.pojos.Author;
import com.example.jooq.tables.records.AuthorRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JooqSelectRepositoryTest {

    @Autowired
    private JooqSelectRepository repository;

    @Test
    @DisplayName("DB에서 전체 작가 레코드를 정상적으로 조회한다")
    void findAllAuthorsTest() {
        // when
        List<AuthorRecord> authors = repository.findAllAuthors();

        // then
        // init-script.sql 에 초기 데이터가 들어있다고 가정
        assertThat(authors).isNotNull();
    }

    @Test
    @DisplayName("조건(이름, ID)에 맞는 작가를 POJO 리스트로 매핑하여 반환한다")
    void findAuthorsByLastNameAndIdTest() {
        // when
        List<Author> authors = repository.findAuthorsByLastNameAndId("S", 0);

        // then
        assertThat(authors).isNotNull();
        // 반환된 작가들의 ID가 모두 0 초과인지 검증합니다.
        assertThat(authors).allSatisfy(a -> assertThat(a.getId()).isGreaterThan(0));
    }
}
