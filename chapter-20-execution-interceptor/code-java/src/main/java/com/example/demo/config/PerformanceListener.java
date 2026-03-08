package com.example.demo.config;

import org.jooq.ExecuteContext;
import org.jooq.impl.DefaultExecuteListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PerformanceListener extends DefaultExecuteListener {

    private static final Logger log = LoggerFactory.getLogger(PerformanceListener.class);
    private static final long SLOW_QUERY_THRESHOLD_MS = 100; // 100ms 이상 지연되면 워닝 로깅

    @Override
    public void executeStart(ExecuteContext ctx) {
        // 쿼리 실행 직전 나노타임 기록. ExecuteContext 의 data 맵은 현재 쿼리의 생명주기 내에서 스레드 안전하게(공유되지 않고) 유지됨
        ctx.data("time", System.nanoTime());
    }

    @Override
    public void executeEnd(ExecuteContext ctx) {
        Long startTime = (Long) ctx.data("time");
        if (startTime != null) {
            long executionTimeMs = (System.nanoTime() - startTime) / 1_000_000;
            
            String sql = ctx.sql();
            if (executionTimeMs > SLOW_QUERY_THRESHOLD_MS) {
                log.warn("[SLOW QUERY] Execution Time: {}ms | SQL: {}", executionTimeMs, sql);
            } else {
                log.info("Query Execution Time: {}ms | SQL: {}", executionTimeMs, sql);
            }
        }
    }
}
