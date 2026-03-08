package com.example.demo.service

import com.example.demo.entity.AuthorEntity
import com.example.demo.repository.AuthorJooqRepository
import com.example.demo.repository.AuthorJpaRepository
import com.example.jooq.tables.pojos.Author
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthorService(
    private val jpaRepository: AuthorJpaRepository,
    private val jooqRepository: AuthorJooqRepository
) {
    /**
     * Anti-pattern
     * save()만 호출 후 jOOQ로 조회 시 읽기 보장 안됨
     */
    @Transactional
    fun saveWithJpaAndReadOmittedFlush(id: Int): Author? {
        val entity = AuthorEntity(id = id, firstName = "NoFlush", lastName = "Test")
        jpaRepository.save(entity)

        return jooqRepository.findById(id)
    }

    /**
     * Best-practice
     * saveAndFlush()를 통해 강제로 DB로 밀어넣은 후 jOOQ로 조회
     */
    @Transactional
    fun saveWithJpaAndReadWithFlush(id: Int): Author? {
        val entity = AuthorEntity(id = id, firstName = "Flushed", lastName = "Test")
        jpaRepository.saveAndFlush(entity)

        return jooqRepository.findById(id)
    }
}
