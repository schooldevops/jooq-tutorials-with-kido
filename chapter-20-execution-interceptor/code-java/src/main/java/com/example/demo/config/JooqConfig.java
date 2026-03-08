package com.example.demo.config;

import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JooqConfig {

    @Bean
    public DefaultExecuteListenerProvider performanceListenerProvider() {
        return new DefaultExecuteListenerProvider(new PerformanceListener());
    }
}
