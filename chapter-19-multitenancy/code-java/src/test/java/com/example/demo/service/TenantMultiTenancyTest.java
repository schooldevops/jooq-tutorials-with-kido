package com.example.demo.service;

import com.example.demo.config.TenantContext;
import org.jooq.DSLContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.BadSqlGrammarException;

import static com.example.jooq.Tables.AUTHOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class TenantMultiTenancyTest {

    @Autowired
    private TenantService tenantService;

    @Autowired
    private DSLContext dsl;

    @Test
    @DisplayName("테넌트 컨텍스트에 따라 실행되는 SQL의 스키마명이 동적으로 변경된다")
    void testTenantRenderMapping() {
        // given & when
        // tenant_a 의 스키마가 현재 DB에 없으므로 (학습용 환경), relation "tenant_a.author" does not exist 에러가 터집니다.
        // 우리는 이 오류를 잡아내어 쿼리 렌더링이 성공적으로 바뀌었는지 증명합니다.
        BadSqlGrammarException exceptionA = assertThrows(
            BadSqlGrammarException.class, 
            () -> tenantService.getAuthorsForTenant("tenant_a")
        );

        // then: 에러 메시지에 우리가 의도한 tenant_a.author 테이블이 포함되어야 한다.
        assertThat(exceptionA.getMessage()).contains("\"tenant_a\".\"author\"");

        // given & when: 또 다른 테넌트로 호출
        BadSqlGrammarException exceptionB = assertThrows(
            BadSqlGrammarException.class, 
            () -> tenantService.getAuthorsForTenant("tenant_b")
        );

        // then: 이번에는 tenant_b.author 이어야 한다.
        assertThat(exceptionB.getMessage()).contains("\"tenant_b\".\"author\"");

        // given & when: 컨텍스트 없이 (기본 public) 직접 호출 시 정상 동작 (기본 스키마에 author가 있으므로)
        TenantContext.clear();
        int count = dsl.selectCount().from(AUTHOR).fetchOne(0, int.class);
        assertThat(count).isGreaterThanOrEqualTo(0); // 에러 안 남
    }
}
