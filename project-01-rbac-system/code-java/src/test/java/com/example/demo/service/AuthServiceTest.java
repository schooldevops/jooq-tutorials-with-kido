package com.example.demo.service;

import com.example.demo.dto.UserProfileDto;
import com.example.jooq.tables.records.PermissionRecord;
import com.example.jooq.tables.records.RolePermissionRecord;
import com.example.jooq.tables.records.RoleRecord;
import com.example.jooq.tables.records.UserRoleRecord;
import com.example.jooq.tables.records.UsersRecord;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static com.example.jooq.tables.Permission.PERMISSION;
import static com.example.jooq.tables.Role.ROLE;
import static com.example.jooq.tables.RolePermission.ROLE_PERMISSION;
import static com.example.jooq.tables.UserRole.USER_ROLE;
import static com.example.jooq.tables.Users.USERS;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private DSLContext dsl;

    private Long adminUserId;
    private Long normalUserId;

    @BeforeEach
    void setUp() {
        // Clear all tables
        dsl.deleteFrom(USER_ROLE).execute();
        dsl.deleteFrom(ROLE_PERMISSION).execute();
        dsl.deleteFrom(PERMISSION).execute();
        dsl.deleteFrom(ROLE).execute();
        dsl.deleteFrom(USERS).execute();

        // 1. Create Roles
        RoleRecord adminRole = dsl.insertInto(ROLE, ROLE.NAME).values("ADMIN").returning().fetchOne();
        RoleRecord userRole = dsl.insertInto(ROLE, ROLE.NAME).values("USER").returning().fetchOne();

        // 2. Create Permissions
        PermissionRecord readArticle = dsl.insertInto(PERMISSION, PERMISSION.RESOURCE, PERMISSION.ACTION)
                .values("ARTICLE", "READ").returning().fetchOne();
        PermissionRecord writeArticle = dsl.insertInto(PERMISSION, PERMISSION.RESOURCE, PERMISSION.ACTION)
                .values("ARTICLE", "WRITE").returning().fetchOne();
        PermissionRecord deleteArticle = dsl.insertInto(PERMISSION, PERMISSION.RESOURCE, PERMISSION.ACTION)
                .values("ARTICLE", "DELETE").returning().fetchOne();

        // 3. Map Permissions to Roles (ADMIN has all, USER has only READ/WRITE)
        dsl.insertInto(ROLE_PERMISSION, ROLE_PERMISSION.ROLE_ID, ROLE_PERMISSION.PERMISSION_ID)
                .values(adminRole.getId(), readArticle.getId())
                .values(adminRole.getId(), writeArticle.getId())
                .values(adminRole.getId(), deleteArticle.getId())
                .values(userRole.getId(), readArticle.getId())
                .values(userRole.getId(), writeArticle.getId())
                .execute();

        // 4. Create Users
        UsersRecord adminUser = dsl.insertInto(USERS, USERS.USERNAME, USERS.EMAIL, USERS.PASSWORD_HASH)
                .values("adminUser", "admin@example.com", "hash").returning().fetchOne();
        UsersRecord normalUser = dsl.insertInto(USERS, USERS.USERNAME, USERS.EMAIL, USERS.PASSWORD_HASH)
                .values("normalUser", "user@example.com", "hash").returning().fetchOne();

        this.adminUserId = adminUser.getId();
        this.normalUserId = normalUser.getId();

        // 5. Map Users to Roles
        dsl.insertInto(USER_ROLE, USER_ROLE.USER_ID, USER_ROLE.ROLE_ID)
                .values(adminUser.getId(), adminRole.getId())
                .values(normalUser.getId(), userRole.getId())
                .execute();
    }

    @Test
    @DisplayName("Multiset 매핑: 관리자는 모든 권한을 가진 계층형 DTO로 반환되어야 한다")
    void testGetUserProfileWithPermissions_Admin() {
        UserProfileDto profile = authService.getUserProfile("adminUser");

        assertThat(profile).isNotNull();
        assertThat(profile.username()).isEqualTo("adminUser");
        assertThat(profile.roles()).hasSize(1);
        
        var role = profile.roles().get(0);
        assertThat(role.name()).isEqualTo("ADMIN");
        assertThat(role.permissions()).hasSize(3);
        assertThat(role.permissions()).extracting("action").containsExactlyInAnyOrder("READ", "WRITE", "DELETE");
    }

    @Test
    @DisplayName("EXISTS 검증: 일반 유저는 ARTICLE DELETE 권한이 없어야 한다")
    void testCheckPermission_NormalUser() {
        boolean canRead = authService.checkPermission(normalUserId, "ARTICLE", "READ");
        boolean canDelete = authService.checkPermission(normalUserId, "ARTICLE", "DELETE");

        assertThat(canRead).isTrue();
        assertThat(canDelete).isFalse();
    }
    
    @Test
    @DisplayName("EXISTS 검증: 관리자는 ARTICLE DELETE 권한이 있어야 한다")
    void testCheckPermission_AdminUser() {
        boolean canDelete = authService.checkPermission(adminUserId, "ARTICLE", "DELETE");

        assertThat(canDelete).isTrue();
    }
}
