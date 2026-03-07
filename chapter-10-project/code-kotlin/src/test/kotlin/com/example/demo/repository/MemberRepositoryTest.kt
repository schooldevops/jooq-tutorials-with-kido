package com.example.demo.repository

import com.example.jooq.tables.pojos.Author
import com.example.jooq.tables.records.AuthorRecord
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    private lateinit var repository: MemberRepository

    @Test
    @DisplayName("findAll(0, 10): 회원 목록을 lastName 오름차순 + 페이징으로 조회한다")
    fun findAllTest() {
        // when
        val members: List<Author> = repository.findAll(0, 10)

        // then
        assertThat(members).isNotNull.isNotEmpty
        for (i in 1 until members.size) {
            assertThat(members[i].lastName).isGreaterThanOrEqualTo(members[i - 1].lastName)
        }
    }

    @Test
    @DisplayName("findById(1): ID=1 회원을 단건 조회한다")
    fun findByIdTest() {
        // when
        val member: Author? = repository.findById(1)

        // then
        assertThat(member).isNotNull
        assertThat(member!!.id).isEqualTo(1)
    }

    @Test
    @DisplayName("create: 새 회원을 등록하면 자동 생성 ID가 포함된 레코드가 반환된다")
    fun createTest() {
        // when
        val record: AuthorRecord = repository.create("Agatha", "Christie")

        // then
        assertThat(record).isNotNull
        assertThat(record.id).isGreaterThan(0)
        assertThat(record.firstName).isEqualTo("Agatha")
        assertThat(record.lastName).isEqualTo("Christie")
    }

    @Test
    @DisplayName("update: 회원 정보를 수정하면 영향 행 수가 1이고 재조회 시 변경된다")
    fun updateTest() {
        // given
        val authorId = 1
        val newLastName = "Shakespeare-Updated"

        // when
        val affected = repository.update(authorId, "William", newLastName)

        // then
        assertThat(affected).isEqualTo(1)
        val updated = repository.findById(authorId)
        assertThat(updated).isNotNull
        assertThat(updated!!.lastName).isEqualTo(newLastName)
    }

    @Test
    @DisplayName("delete: 새로 생성한 회원을 삭제하면 조회 결과가 null이 된다")
    fun deleteTest() {
        // given
        val created: AuthorRecord = repository.create("Test", "User")
        val newId = created.id!!

        // when
        val affected = repository.delete(newId)

        // then
        assertThat(affected).isEqualTo(1)
        val deleted = repository.findById(newId)
        assertThat(deleted).isNull()
    }
}
