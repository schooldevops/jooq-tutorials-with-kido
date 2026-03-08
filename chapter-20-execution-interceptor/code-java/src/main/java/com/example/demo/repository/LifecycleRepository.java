package com.example.demo.repository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
public class LifecycleRepository {

    private final DSLContext dsl;

    public LifecycleRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public void executeFastQuery() {
        // 빠른 단건 조회 (dual 같은 의미 없는 쿼리)
        dsl.selectOne().fetch();
    }

    public void executeSlowQuery() {
        // 강제로 1초 지연시키는 쿼리 호출 (PostgreSQL)
        dsl.select(org.jooq.impl.DSL.field("pg_sleep(1)")).fetch();
    }
}
