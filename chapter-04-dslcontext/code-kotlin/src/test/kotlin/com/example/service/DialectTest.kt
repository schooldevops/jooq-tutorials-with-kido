package com.example.service

import org.assertj.core.api.Assertions.assertThat
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class DialectTest {

    @Test
    @DisplayName("코틀린 환경: PostgreSQL 방언은 FETCH NEXT 절을 사용한다")
    fun postgresDialectTest() {
        val dsl = DSL.using(SQLDialect.POSTGRES)
        val sql = dsl.selectFrom(DSL.table("users")).limit(10).getSQL()

        assertThat(sql).containsIgnoringCase("fetch next")
    }

    @Test
    @DisplayName("코틀린 환경: DERBY 방언은 FETCH NEXT 기반으로 쿼리를 렌더링한다")
    fun derbyDialectTest() {
        // 오픈소스 버전의 jOOQ(spring-boot-starter-jooq에 포함)에서 지원하는 DERBY 방언 테스트
        val dsl = DSL.using(SQLDialect.DERBY)
        val sql = dsl.selectFrom(DSL.table("users")).limit(10).getSQL()

        assertThat(sql).containsIgnoringCase("fetch next")
    }
}
