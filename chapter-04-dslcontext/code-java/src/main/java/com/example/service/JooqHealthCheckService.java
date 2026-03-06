package com.example.service;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JooqHealthCheckService {

    // Spring Boot Auto-configuration에 의해 DSLContext가 자동으로 주입됩니다.
    private final DSLContext dsl;

    public Integer checkHealth() {
        // 가장 단순한 쿼리를 날려봅니다. (SELECT 1)
        return dsl.selectOne().fetchOneInto(Integer.class);
    }
}
