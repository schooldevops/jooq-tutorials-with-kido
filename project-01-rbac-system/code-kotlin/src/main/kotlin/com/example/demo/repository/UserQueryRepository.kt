package com.example.demo.repository

import com.example.demo.dto.PermissionDto
import com.example.demo.dto.RoleDto
import com.example.demo.dto.UserProfileDto
import com.example.jooq.tables.Permission.PERMISSION
import com.example.jooq.tables.Role.ROLE
import com.example.jooq.tables.RolePermission.ROLE_PERMISSION
import com.example.jooq.tables.Users.USERS
import com.example.jooq.tables.UserRole.USER_ROLE
import org.jooq.DSLContext
import org.jooq.Records.mapping
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.select
import org.springframework.stereotype.Repository

@Repository
class UserQueryRepository(private val dsl: DSLContext) {

    fun getUserProfileWithPermissions(username: String): UserProfileDto? {
        return dsl.select(
            USERS.ID,
            USERS.USERNAME,
            USERS.STATUS,
            multiset(
                select(
                    ROLE.ID,
                    ROLE.NAME,
                    multiset(
                        select(PERMISSION.ID, PERMISSION.RESOURCE, PERMISSION.ACTION)
                        .from(ROLE_PERMISSION)
                        .join(PERMISSION).on(ROLE_PERMISSION.PERMISSION_ID.eq(PERMISSION.ID))
                        .where(ROLE_PERMISSION.ROLE_ID.eq(ROLE.ID))
                    ).`as`("permissions").convertFrom { r -> r.map(mapping(::PermissionDto)) }
                )
                .from(USER_ROLE)
                .join(ROLE).on(USER_ROLE.ROLE_ID.eq(ROLE.ID))
                .where(USER_ROLE.USER_ID.eq(USERS.ID))
            ).`as`("roles").convertFrom { r -> r.map(mapping(::RoleDto)) }
        )
        .from(USERS)
        .where(USERS.USERNAME.eq(username))
        .fetchOneInto(UserProfileDto::class.java)
    }
}
