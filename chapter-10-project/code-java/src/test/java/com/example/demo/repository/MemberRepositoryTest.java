package com.example.demo.repository;

import com.example.jooq.tables.pojos.Author;
import com.example.jooq.tables.records.AuthorRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    private MemberRepository repository;

    @Test
    @DisplayName("findAll(0, 10): 회원 목록을 lastName 오름차순 + 페이징으로 조회한다")
    void findAllTest() {
        // when
        List<Author> members = repository.findAll(0, 10);

        // then
        assertThat(members).isNotNull().isNotEmpty();
        // lastName 오름차순 검증
        for (int i = 1; i < members.size(); i++) {
            assertThat(members.get(i).getLastName())
                .isGreaterThanOrEqualTo(members.get(i - 1).getLastName());
        }
    }

    @Test
    @DisplayName("findById(1): ID=1 회원을 단건 조회한다")
    void findByIdTest() {
        // when
        Optional<Author> member = repository.findById(1);

        // then
        assertThat(member).isPresent();
        assertThat(member.get().getId()).isEqualTo(1);
    }

    @Test
    @DisplayName("create: 새 회원을 등록하면 자동 생성 ID가 포함된 레코드가 반환된다")
    void createTest() {
        // when
        AuthorRecord record = repository.create("Agatha", "Christie");

        // then
        assertThat(record).isNotNull();
        assertThat(record.getId()).isGreaterThan(0);
        assertThat(record.getFirstName()).isEqualTo("Agatha");
        assertThat(record.getLastName()).isEqualTo("Christie");
    }

    @Test
    @DisplayName("update: 회원 정보를 수정하면 영향 행 수가 1이고 재조회 시 변경된다")
    void updateTest() {
        // given
        int authorId = 1;
        String newLastName = "Shakespeare-Updated";

        // when
        int affected = repository.update(authorId, "William", newLastName);

        // then
        assertThat(affected).isEqualTo(1);
        Optional<Author> updated = repository.findById(authorId);
        assertThat(updated).isPresent();
        assertThat(updated.get().getLastName()).isEqualTo(newLastName);
    }

    @Test
    @DisplayName("delete: 새로 생성한 회원을 삭제하면 조회 결과가 empty가 된다")
    void deleteTest() {
        // given: 새 회원 등록
        AuthorRecord created = repository.create("Test", "User");
        int newId = created.getId();

        // when
        int affected = repository.delete(newId);

        // then
        assertThat(affected).isEqualTo(1);
        Optional<Author> deleted = repository.findById(newId);
        assertThat(deleted).isEmpty();
    }
}
