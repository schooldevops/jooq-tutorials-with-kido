package com.example.demo.service;

import com.example.demo.entity.AuthorEntity;
import com.example.demo.repository.AuthorJooqRepository;
import com.example.demo.repository.AuthorJpaRepository;
import com.example.jooq.tables.pojos.Author;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorJpaRepository jpaRepository;
    private final AuthorJooqRepository jooqRepository;

    /**
     * Anti-pattern (Write-Behind 이슈)
     * save()만 호출 후 바로 jOOQ로 조회하면 1차 캐시에만 있고 DB에는 반영되지 않아 찾을 수 없음.
     */
    @Transactional
    public Optional<Author> saveWithJpaAndReadOmittedFlush(int id) {
        AuthorEntity entity = new AuthorEntity(id, "NoFlush", "Test");
        jpaRepository.save(entity);

        // jOOQ로 조회 시도! (DB를 직접 찌르기 때문에 NoFlush 데이터가 나오지 않음)
        return jooqRepository.findById(id);
    }

    /**
     * Best-practice (CQRS 동기화)
     * saveAndFlush()를 통해 강제로 DB로 Insert를 밀어넣은 후 jOOQ로 안전하게 조회함.
     */
    @Transactional
    public Optional<Author> saveWithJpaAndReadWithFlush(int id) {
        AuthorEntity entity = new AuthorEntity(id, "Flushed", "Test");
        
        // 메모리에 써둔 영속 상태를 DB로 강제 Flush
        jpaRepository.saveAndFlush(entity);

        // jOOQ로 안전하게 DB 조회 가능!
        return jooqRepository.findById(id);
    }
}
