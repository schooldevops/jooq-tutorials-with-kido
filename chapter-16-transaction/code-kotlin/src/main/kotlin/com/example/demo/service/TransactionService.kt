package com.example.demo.service

import com.example.demo.repository.TransactionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 【Chapter 16】 트랜잭션과 스프링 통합 - Kotlin
 * 비즈니스 로직과 트랜잭션(Rollback/Commit) 동작 확인
 */
@Service
class TransactionService(
    private val repository: TransactionRepository
) {

    @Transactional
    fun saveAuthorAndBookSuccessfully(authorId: Int) {
        repository.saveAuthor(authorId, "Tx", "Success")
        // ... 정상 반환됨 -> 커밋
    }

    @Transactional
    fun saveAuthorAndThrowException(authorId: Int) {
        repository.saveAuthor(authorId, "Tx", "Fail")
        // 고의로 예외 발생 -> 스프링에 의해 해당 메서드 내 모든 DB 수행이 롤백됨
        throw RuntimeException("Intentional Exception for Rollback")
    }
}
