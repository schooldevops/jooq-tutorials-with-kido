package com.example.service;

import static org.assertj.core.api.Assertions.assertThat;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DialectTest {

    @Test
    @DisplayName("PostgreSQL 방언은 FETCH NEXT 절을 사용한다")
    void postgresDialectTest() {
        // given
        DSLContext dsl = DSL.using(SQLDialect.POSTGRES);

        // when
        // 쿼리를 실행하지 않고 SQL 문자열로 변환(Rendering)만 수행합니다.
        String sql = dsl.selectFrom(DSL.table("users")).limit(10).getSQL();

        // тогда
        assertThat(sql).containsIgnoringCase("fetch next");
    }

    @Test
    @DisplayName("DERBY 방언은 FETCH NEXT 기반으로 쿼리를 렌더링한다")
    void derbyDialectTest() {
        // given
        DSLContext dsl = DSL.using(SQLDialect.DERBY);

        // when
        String sql = dsl.selectFrom(DSL.table("users")).limit(10).getSQL();

        // then
        assertThat(sql).containsIgnoringCase("fetch next");
    }
}
