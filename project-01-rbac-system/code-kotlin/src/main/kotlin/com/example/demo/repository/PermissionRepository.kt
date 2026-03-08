package com.example.demo.repository

import com.example.jooq.tables.Permission.PERMISSION
import com.example.jooq.tables.RolePermission.ROLE_PERMISSION
import com.example.jooq.tables.UserRole.USER_ROLE
import org.jooq.DSLContext
import org.jooq.impl.DSL.selectOne
import org.springframework.stereotype.Repository

@Repository
class PermissionRepository(private val dsl: DSLContext) {

    fun hasPermission(userId: Long, resource: String, action: String): Boolean {
        return dsl.fetchExists(
            selectOne()
            .from(USER_ROLE)
            .join(ROLE_PERMISSION).on(USER_ROLE.ROLE_ID.eq(ROLE_PERMISSION.ROLE_ID))
            .join(PERMISSION).on(ROLE_PERMISSION.PERMISSION_ID.eq(PERMISSION.ID))
            .where(USER_ROLE.USER_ID.eq(userId))
              .and(PERMISSION.RESOURCE.eq(resource))
              .and(PERMISSION.ACTION.eq(action))
        )
    }
}
