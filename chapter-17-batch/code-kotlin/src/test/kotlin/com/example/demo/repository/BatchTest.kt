package com.example.demo.repository

import com.example.jooq.tables.pojos.Author
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.system.measureTimeMillis

@SpringBootTest
class BatchTest {

    private val log = LoggerFactory.getLogger(BatchTest::class.java)

    @Autowired
    private lateinit var repository: BatchRepository

    companion object {
        const val BATCH_SIZE = 1000
        const val STARTING_ID_SINGLE = 10000
        const val STARTING_ID_BATCH = 20000
    }

    @AfterEach
    fun tearDown() {
        repository.deleteAllGenerated(STARTING_ID_SINGLE)
    }

    private fun createDummyAuthors(startId: Int): List<Author> {
        return (0 until BATCH_SIZE).map { i ->
            Author().apply {
                id = startId + i
                firstName = "BatchName$i"
                lastName = "BatchLast$i"
            }
        }
    }

    @Test
    @DisplayName("성능 비교: 배치 Insert가 단건 반복 Insert보다 빠르다")
    fun compareBatchPerformance() {
        val singleList = createDummyAuthors(STARTING_ID_SINGLE)
        val batchList = createDummyAuthors(STARTING_ID_BATCH)

        // 웜업 (성능 측정 제외)
        repository.insertSingleBySingle(listOf(
            Author().apply { id = 9999; firstName = "Warmup"; lastName = "Warmup" }
        ))
        repository.deleteAllGenerated(9999)

        // 1. 단건 인서트 측정
        val timeSingle = measureTimeMillis {
            repository.insertSingleBySingle(singleList)
        }

        // 2. 배치 인서트 측정
        val timeBatch = measureTimeMillis {
            repository.insertInBatch(batchList)
        }

        log.info("============== PERFORMANCE RESULT ==============")
        log.info("[1000건 삽입] 단건 반복 Insert 소요 시간: {} ms", timeSingle)
        log.info("[1000건 삽입] 배치(Batch) Insert 소요 시간: {} ms", timeBatch)
        log.info("================================================")

        assertThat(timeBatch).isLessThan(timeSingle)
    }
}
