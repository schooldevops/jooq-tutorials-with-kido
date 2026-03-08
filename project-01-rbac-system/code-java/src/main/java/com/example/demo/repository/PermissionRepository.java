package com.example.demo.repository;

import org.jooq.DSLContext;
import static org.jooq.impl.DSL.selectOne;
import org.springframework.stereotype.Repository;

import static com.example.jooq.tables.Permission.PERMISSION;
import static com.example.jooq.tables.RolePermission.ROLE_PERMISSION;
import static com.example.jooq.tables.UserRole.USER_ROLE;

@Repository
public class PermissionRepository {
    private final DSLContext dsl;

    public PermissionRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public boolean hasPermission(Long userId, String resource, String action) {
        return dsl.fetchExists(
            selectOne()
            .from(USER_ROLE)
            .join(ROLE_PERMISSION).on(USER_ROLE.ROLE_ID.eq(ROLE_PERMISSION.ROLE_ID))
            .join(PERMISSION).on(ROLE_PERMISSION.PERMISSION_ID.eq(PERMISSION.ID))
            .where(USER_ROLE.USER_ID.eq(userId))
              .and(PERMISSION.RESOURCE.eq(resource))
              .and(PERMISSION.ACTION.eq(action))
        );
    }
}
