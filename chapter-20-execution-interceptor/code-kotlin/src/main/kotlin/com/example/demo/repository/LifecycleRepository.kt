package com.example.demo.repository

import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository

@Repository
class LifecycleRepository(private val dsl: DSLContext) {

    fun executeFastQuery() {
        // 빠른 단건 조회 (dual 같은 의미 없는 쿼리)
        dsl.selectOne().fetch()
    }

    fun executeSlowQuery() {
        // 강제로 1초 지연시키는 쿼리 호출 (PostgreSQL)
        dsl.select(DSL.field("pg_sleep(1)")).fetch()
    }
}
