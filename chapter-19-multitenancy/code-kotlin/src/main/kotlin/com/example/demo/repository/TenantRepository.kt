package com.example.demo.repository

import com.example.demo.config.TenantContext
import com.example.jooq.tables.references.AUTHOR
import com.example.jooq.tables.pojos.Author
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class TenantRepository(private val dsl: DSLContext) {

    fun findAllAuthors(): List<Author> {
        val tenant = TenantContext.getCurrentTenant()
        var tenantDsl = dsl

        if (!tenant.isNullOrEmpty()) {
            try {
                // Settings 객체를 복제하여 전역 설정 오염을 방지하고 RenderMapping 적용
                val clonedSettings = dsl.settings().clone() as org.jooq.conf.Settings
                clonedSettings.renderMapping = org.jooq.conf.RenderMapping()
                    .withSchemata(
                        org.jooq.conf.MappedSchema().withInput("public").withOutput(tenant)
                    )
                tenantDsl = dsl.configuration().derive(clonedSettings).dsl()
            } catch (e: Exception) {
                throw RuntimeException("Settings clone failed", e)
            }
        }

        return tenantDsl.selectFrom(AUTHOR)
            .fetchInto(Author::class.java)
    }
}
