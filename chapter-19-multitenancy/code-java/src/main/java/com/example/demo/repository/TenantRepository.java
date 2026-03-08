package com.example.demo.repository;

import com.example.jooq.tables.pojos.Author;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.example.demo.config.TenantContext;

import static com.example.jooq.Tables.AUTHOR;

@Repository
public class TenantRepository {

    private final DSLContext dsl;

    public TenantRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public List<Author> findAllAuthors() {
        String tenant = TenantContext.getCurrentTenant();
        DSLContext tenantDsl = dsl;
        
        if (tenant != null && !tenant.isEmpty()) {
            try {
                org.jooq.conf.Settings clonedSettings = (org.jooq.conf.Settings) dsl.settings().clone();
                clonedSettings.setRenderMapping(new org.jooq.conf.RenderMapping()
                    .withSchemata(
                        new org.jooq.conf.MappedSchema().withInput("public").withOutput(tenant)
                    )
                );
                tenantDsl = dsl.configuration().derive(clonedSettings).dsl();
            } catch (Exception e) {
                // clone 에러 무시 또는 로깅
                throw new RuntimeException("Settings clone failed", e);
            }
        }

        return tenantDsl.selectFrom(AUTHOR)
                  .fetchInto(Author.class);
    }
}
