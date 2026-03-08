package com.example.demo.service;

import com.example.demo.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 【Chapter 16】 트랜잭션과 스프링 통합
 * 비즈니스 로직과 트랜잭션 경계를 담당하는 Service 계층
 */
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository repository;

    /**
     * 정상 커밋 케이스
     * @Transactional 이 선언되면 Spring의 트랜잭션 관리에 Jooq 구문도 함께 포함됩니다.
     */
    @Transactional
    public void saveAuthorAndBookSuccessfully(int authorId) {
        // 1. 저자 저장
        repository.saveAuthor(authorId, "Tx", "Success");
        
        // 추가 로직이 있다고 가정... 정상 종료되면 Commit 됨
    }

    /**
     * 롤백 케이스
     * 저자를 저장하지만, 직후 RuntimeException이 발생하여 전체 트랜잭션이 롤백됩니다.
     */
    @Transactional
    public void saveAuthorAndThrowException(int authorId) {
        // 1. 저자 저장 (이 시점엔 DB 트랜잭션 내에 들어감)
        repository.saveAuthor(authorId, "Tx", "Fail");

        // 2. 강제 예외 발생 -> Spring이 트랜잭션 롤백 처리 (jOOQ 작업도 롤백)
        throw new RuntimeException("Intentional Exception for Rollback");
    }
}
