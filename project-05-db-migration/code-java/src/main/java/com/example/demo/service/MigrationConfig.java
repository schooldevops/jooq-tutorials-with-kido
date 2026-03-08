package com.example.demo.service;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MigrationConfig {

    @Primary
    @Bean(name = "legacyDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.legacy")
    public DataSource legacyDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "targetDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.target")
    public DataSource targetDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "legacyDsl")
    public DSLContext legacyDsl(@Qualifier("legacyDataSource") DataSource dataSource) {
        return DSL.using(dataSource, SQLDialect.POSTGRES);
    }

    @Bean(name = "targetDsl")
    public DSLContext targetDsl(@Qualifier("targetDataSource") DataSource dataSource) {
        return DSL.using(dataSource, SQLDialect.POSTGRES);
    }
}
