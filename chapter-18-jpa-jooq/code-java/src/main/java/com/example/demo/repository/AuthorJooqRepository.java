package com.example.demo.repository;

import com.example.jooq.tables.pojos.Author;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.example.jooq.Tables.AUTHOR;

/**
 * 【Chapter 18】 CQRS Query(Read) 영역: jOOQ
 */
@Repository
@RequiredArgsConstructor
public class AuthorJooqRepository {

    private final DSLContext dsl;

    public Optional<Author> findById(int id) {
        return dsl.selectFrom(AUTHOR)
                  .where(AUTHOR.ID.eq(id))
                  .fetchOptionalInto(Author.class);
    }
}
