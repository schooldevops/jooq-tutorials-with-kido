package com.example.demo.service

import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import javax.sql.DataSource

@Configuration
class MigrationConfig {

    @Primary
    @Bean(name = ["legacyDataSource"])
    @ConfigurationProperties(prefix = "spring.datasource.legacy")
    fun legacyDataSource(): DataSource {
        return DataSourceBuilder.create().build()
    }

    @Bean(name = ["targetDataSource"])
    @ConfigurationProperties(prefix = "spring.datasource.target")
    fun targetDataSource(): DataSource {
        return DataSourceBuilder.create().build()
    }

    @Primary
    @Bean(name = ["legacyDsl"])
    fun legacyDsl(@Qualifier("legacyDataSource") dataSource: DataSource): DSLContext {
        return DSL.using(dataSource, SQLDialect.POSTGRES)
    }

    @Bean(name = ["targetDsl"])
    fun targetDsl(@Qualifier("targetDataSource") dataSource: DataSource): DSLContext {
        return DSL.using(dataSource, SQLDialect.POSTGRES)
    }
}
