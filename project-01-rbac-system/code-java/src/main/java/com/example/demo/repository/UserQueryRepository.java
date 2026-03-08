package com.example.demo.repository;

import org.jooq.DSLContext;
import static org.jooq.Records.mapping;
import static org.jooq.impl.DSL.multiset;
import static org.jooq.impl.DSL.select;
import org.springframework.stereotype.Repository;

import com.example.demo.dto.PermissionDto;
import com.example.demo.dto.RoleDto;
import com.example.demo.dto.UserProfileDto;
import static com.example.jooq.tables.Permission.PERMISSION;
import static com.example.jooq.tables.Role.ROLE;
import static com.example.jooq.tables.RolePermission.ROLE_PERMISSION;
import static com.example.jooq.tables.UserRole.USER_ROLE;
import static com.example.jooq.tables.Users.USERS;

@Repository
public class UserQueryRepository {
    private final DSLContext dsl;

    public UserQueryRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public UserProfileDto getUserProfileWithPermissions(String username) {
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
                        ).as("permissions").convertFrom(r -> r.map(mapping(PermissionDto::new)))
                    )
                    .from(USER_ROLE)
                    .join(ROLE).on(USER_ROLE.ROLE_ID.eq(ROLE.ID))
                    .where(USER_ROLE.USER_ID.eq(USERS.ID))
                ).as("roles").convertFrom(r -> r.map(mapping(RoleDto::new)))
            )
            .from(USERS)
            .where(USERS.USERNAME.eq(username))
            .fetchOneInto(UserProfileDto.class);
    }
}
