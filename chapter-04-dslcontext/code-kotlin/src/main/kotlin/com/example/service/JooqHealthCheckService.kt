package com.example.service

import org.jooq.DSLContext
import org.springframework.stereotype.Service

@Service
class JooqHealthCheckService(
    // 생성자 주입 방식으로 Spring Boot가 DSLContext 빈을 자동 연결해 줍니다.
    private val dsl: DSLContext
) {
    fun checkHealth(): Int? {
        // 코틀린 환경에서도 직관적인 jOOQ DSL 사용
        return dsl.selectOne().fetchOneInto(Int::class.java)
    }
}
