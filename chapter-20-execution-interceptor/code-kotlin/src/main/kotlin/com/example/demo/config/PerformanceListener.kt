package com.example.demo.config

import org.jooq.ExecuteContext
import org.jooq.impl.DefaultExecuteListener
import org.slf4j.LoggerFactory

class PerformanceListener : DefaultExecuteListener() {

    private val log = LoggerFactory.getLogger(PerformanceListener::class.java)
    private val SLOW_QUERY_THRESHOLD_MS: Long = 100 // 100ms 이상 지연되면 워닝 로깅

    override fun executeStart(ctx: ExecuteContext) {
        // 쿼리 실행 직전 나노타임 기록
        ctx.data("time", System.nanoTime())
    }

    override fun executeEnd(ctx: ExecuteContext) {
        val startTime = ctx.data("time") as? Long
        if (startTime != null) {
            val executionTimeMs = (System.nanoTime() - startTime) / 1_000_000
            val sql = ctx.sql()

            if (executionTimeMs > SLOW_QUERY_THRESHOLD_MS) {
                log.warn("[SLOW QUERY] Execution Time: {}ms | SQL: {}", executionTimeMs, sql)
            } else {
                log.info("Query Execution Time: {}ms | SQL: {}", executionTimeMs, sql)
            }
        }
    }
}
