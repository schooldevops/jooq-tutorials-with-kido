package com.example.demo.config

import org.jooq.impl.DefaultExecuteListenerProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JooqConfig {

    @Bean
    fun performanceListenerProvider(): DefaultExecuteListenerProvider {
        return DefaultExecuteListenerProvider(PerformanceListener())
    }
}
