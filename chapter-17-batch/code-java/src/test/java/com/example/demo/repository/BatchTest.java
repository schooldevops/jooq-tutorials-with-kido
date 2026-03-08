package com.example.demo.repository;

import com.example.jooq.tables.pojos.Author;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BatchTest {

    private static final Logger log = LoggerFactory.getLogger(BatchTest.class);

    @Autowired
    private BatchRepository repository;

    private static final int BATCH_SIZE = 1000;
    private static final int STARTING_ID_SINGLE = 10000;
    private static final int STARTING_ID_BATCH = 20000;

    @AfterEach
    void tearDown() {
        // 테스트 후 데이터 정리
        repository.deleteAllGenerated(STARTING_ID_SINGLE);
    }

    private List<Author> createDummyAuthors(int startId) {
        List<Author> list = new ArrayList<>();
        for (int i = 0; i < BATCH_SIZE; i++) {
            Author a = new Author();
            a.setId(startId + i);
            a.setFirstName("BatchName" + i);
            a.setLastName("BatchLast" + i);
            list.add(a);
        }
        return list;
    }

    @Test
    @DisplayName("성능 비교: 배치 Insert가 단건 반복 Insert보다 확연히 빠르다")
    void compareBatchPerformance() {
        // given
        List<Author> singleList = createDummyAuthors(STARTING_ID_SINGLE);
        List<Author> batchList = createDummyAuthors(STARTING_ID_BATCH);

        // 첫 쿼리 웜업용 (시간 측정에서 제외)
        repository.insertSingleBySingle(List.of(
            new Author(9999, "Warmup", "Warmup")
        ));
        repository.deleteAllGenerated(9999);

        // when 1: 단건 인서트 측정
        long startSingle = System.currentTimeMillis();
        repository.insertSingleBySingle(singleList);
        long endSingle = System.currentTimeMillis();
        long timeSingle = endSingle - startSingle;

        // when 2: 배치 인서트 측정
        long startBatch = System.currentTimeMillis();
        repository.insertInBatch(batchList);
        long endBatch = System.currentTimeMillis();
        long timeBatch = endBatch - startBatch;

        // then
        log.info("============== PERFORMANCE RESULT ==============");
        log.info("[1000건 삽입] 단건 반복 Insert 소요 시간: {} ms", timeSingle);
        log.info("[1000건 삽입] 배치(Batch) Insert 소요 시간: {} ms", timeBatch);
        log.info("================================================");

        // 배치 성능이 단건보다 좋아야 함 (일반적으로 5~10배 이상 빠름)
        assertThat(timeBatch).isLessThan(timeSingle);
    }
}
