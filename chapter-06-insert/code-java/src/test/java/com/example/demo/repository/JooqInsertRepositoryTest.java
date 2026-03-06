package com.example.demo.repository;

import com.example.jooq.tables.records.AuthorRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional // 각 테스트 종료 후 자동 롤백으로 DB 클린 유지
class JooqInsertRepositoryTest {

    @Autowired
    private JooqInsertRepository repository;

    @Test
    @DisplayName("DSL 스타일로 작가를 삽입하면 영향받은 행 수가 1이다")
    void insertAuthorDslTest() {
        // given
        String firstName = "Leo";
        String lastName = "Tolstoy";

        // when
        int affectedRows = repository.insertAuthorDsl(firstName, lastName);

        // then
        assertThat(affectedRows).isEqualTo(1);
    }

    @Test
    @DisplayName("UpdatableRecord 방식으로 작가를 삽입하면 DB generated ID가 채워진 레코드를 반환한다")
    void insertAuthorWithRecordTest() {
        // given
        String firstName = "Franz";
        String lastName = "Kafka";

        // when
        AuthorRecord record = repository.insertAuthorWithRecord(firstName, lastName);

        // then
        assertThat(record).isNotNull();
        assertThat(record.getId()).isNotNull(); // SERIAL로 생성된 PK
        assertThat(record.getId()).isGreaterThan(0);
        assertThat(record.getFirstName()).isEqualTo(firstName);
        assertThat(record.getLastName()).isEqualTo(lastName);
    }

    @Test
    @DisplayName("INSERT ... RETURNING으로 책을 삽입하면 DB가 생성한 PK를 즉시 반환한다")
    void insertBookReturningIdTest() {
        // given: 먼저 테스트용 작가를 삽입하고 생성된 ID를 얻음
        AuthorRecord author = repository.insertAuthorWithRecord("Victor", "Hugo");
        int authorId = author.getId();

        // when
        Integer newBookId = repository.insertBookReturningId("Les Misérables", authorId, 1862);

        // then
        assertThat(newBookId).isNotNull();
        assertThat(newBookId).isGreaterThan(0);
    }
}
