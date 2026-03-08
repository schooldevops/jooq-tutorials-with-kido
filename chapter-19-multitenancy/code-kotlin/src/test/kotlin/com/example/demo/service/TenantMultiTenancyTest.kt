package com.example.demo.service

import com.example.demo.config.TenantContext
import com.example.jooq.tables.references.AUTHOR
import org.assertj.core.api.Assertions.assertThat
import org.jooq.DSLContext
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.BadSqlGrammarException

@SpringBootTest
class TenantMultiTenancyTest @Autowired constructor(
    private val tenantService: TenantService,
    private val dsl: DSLContext
) {

    @Test
    @DisplayName("테넌트 컨텍스트에 따라 실행되는 SQL의 스키마명이 동적으로 변경된다")
    fun testTenantRenderMapping() {
        // given & when
        val exceptionA = assertThrows<BadSqlGrammarException> {
            tenantService.getAuthorsForTenant("tenant_a")
        }

        // then: 에러 메시지에 우리가 의도한 tenant_a.author 테이블이 포함되어야 한다.
        assertThat(exceptionA.message).contains("\"tenant_a\".\"author\"")

        // given & when: 또 다른 테넌트로 호출
        val exceptionB = assertThrows<BadSqlGrammarException> {
            tenantService.getAuthorsForTenant("tenant_b")
        }

        // then: 이번에는 tenant_b.author 이어야 한다.
        assertThat(exceptionB.message).contains("\"tenant_b\".\"author\"")

        // given & when: 컨텍스트 없이 (기본 public) 직접 호출 시 정상 동작 (기본 스키마에 author가 있으므로)
        TenantContext.clear()
        val count = dsl.selectCount().from(AUTHOR).fetchOne(0, Int::class.java)
        
        // jooq_demo 데이터베이스의 public 스키마에는 테이블들이 모두 존재하므로 오류가 발생하지 않아야 합니다.
        assertThat(count).isGreaterThanOrEqualTo(0)
    }
}
