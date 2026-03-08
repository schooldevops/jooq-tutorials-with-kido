package com.example.demo.repository;

import com.example.jooq.tables.pojos.Author;
import com.example.jooq.tables.records.AuthorRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.jooq.Tables.AUTHOR;

/**
 * 【Chapter 17】 배치(Batch) 처리와 성능
 */
@Repository
@RequiredArgsConstructor
public class BatchRepository {

    private final DSLContext dsl;

    /**
     * Anti-pattern: 단건 Insert 반복
     * 루프를 돌며 1건씩 DB로 네트워크 I/O 전송
     */
    public void insertSingleBySingle(List<Author> authors) {
        for (Author author : authors) {
            dsl.insertInto(AUTHOR)
               .set(AUTHOR.ID, author.getId())
               .set(AUTHOR.FIRST_NAME, author.getFirstName())
               .set(AUTHOR.LAST_NAME, author.getLastName())
               .execute();
        }
    }

    /**
     * Best-practice: jOOQ batchInsert()
     * 모든 레코드를 메모리에 모았다가 한 번의 JDBC Batch API로 전송 (성능 10배 이상 향상)
     */
    public void insertInBatch(List<Author> authors) {
        List<AuthorRecord> records = authors.stream()
                .map(a -> {
                    AuthorRecord record = dsl.newRecord(AUTHOR);
                    record.setId(a.getId());
                    record.setFirstName(a.getFirstName());
                    record.setLastName(a.getLastName());
                    return record;
                })
                .toList();

        // JDBC의 executeBatch() 활용
        dsl.batchInsert(records).execute();
    }
    
    // 검증/정리용
    public void deleteAllGenerated(int startingId) {
        dsl.deleteFrom(AUTHOR)
           .where(AUTHOR.ID.ge(startingId))
           .execute();
    }
}
