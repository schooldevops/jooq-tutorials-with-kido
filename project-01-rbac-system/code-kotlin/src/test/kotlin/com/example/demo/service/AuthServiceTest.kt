package com.example.demo.service

import com.example.jooq.tables.Permission.PERMISSION
import com.example.jooq.tables.Role.ROLE
import com.example.jooq.tables.RolePermission.ROLE_PERMISSION
import com.example.jooq.tables.Users.USERS
import com.example.jooq.tables.UserRole.USER_ROLE
import org.assertj.core.api.Assertions.assertThat
import org.jooq.DSLContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class AuthServiceTest @Autowired constructor(
    private val authService: AuthService,
    private val dsl: DSLContext
) {

    private var adminUserId: Long = 0
    private var normalUserId: Long = 0

    @BeforeEach
    fun setUp() {
        // Clear all tables
        dsl.deleteFrom(USER_ROLE).execute()
        dsl.deleteFrom(ROLE_PERMISSION).execute()
        dsl.deleteFrom(PERMISSION).execute()
        dsl.deleteFrom(ROLE).execute()
        dsl.deleteFrom(USERS).execute()

        // 1. Create Roles
        val adminRole = dsl.insertInto(ROLE, ROLE.NAME).values("ADMIN").returning().fetchOne()!!
        val userRole = dsl.insertInto(ROLE, ROLE.NAME).values("USER").returning().fetchOne()!!

        // 2. Create Permissions
        val readArticle = dsl.insertInto(PERMISSION, PERMISSION.RESOURCE, PERMISSION.ACTION)
            .values("ARTICLE", "READ").returning().fetchOne()!!
        val writeArticle = dsl.insertInto(PERMISSION, PERMISSION.RESOURCE, PERMISSION.ACTION)
            .values("ARTICLE", "WRITE").returning().fetchOne()!!
        val deleteArticle = dsl.insertInto(PERMISSION, PERMISSION.RESOURCE, PERMISSION.ACTION)
            .values("ARTICLE", "DELETE").returning().fetchOne()!!

        // 3. Map Permissions to Roles (ADMIN has all, USER has only READ/WRITE)
        dsl.insertInto(ROLE_PERMISSION, ROLE_PERMISSION.ROLE_ID, ROLE_PERMISSION.PERMISSION_ID)
            .values(adminRole.id, readArticle.id)
            .values(adminRole.id, writeArticle.id)
            .values(adminRole.id, deleteArticle.id)
            .values(userRole.id, readArticle.id)
            .values(userRole.id, writeArticle.id)
            .execute()

        // 4. Create Users
        val adminUser = dsl.insertInto(USERS, USERS.USERNAME, USERS.EMAIL, USERS.PASSWORD_HASH)
            .values("adminUser", "admin@example.com", "hash").returning().fetchOne()!!
        val normalUser = dsl.insertInto(USERS, USERS.USERNAME, USERS.EMAIL, USERS.PASSWORD_HASH)
            .values("normalUser", "user@example.com", "hash").returning().fetchOne()!!

        this.adminUserId = adminUser.id!!
        this.normalUserId = normalUser.id!!

        // 5. Map Users to Roles
        dsl.insertInto(USER_ROLE, USER_ROLE.USER_ID, USER_ROLE.ROLE_ID)
            .values(adminUser.id, adminRole.id)
            .values(normalUser.id, userRole.id)
            .execute()
    }

    @Test
    @DisplayName("Multiset 매핑: 관리자는 모든 권한을 가진 계층형 DTO로 반환되어야 한다")
    fun testGetUserProfileWithPermissions_Admin() {
        val profile = authService.getUserProfile("adminUser")

        assertThat(profile).isNotNull
        assertThat(profile?.username).isEqualTo("adminUser")
        assertThat(profile?.roles).hasSize(1)

        val role = profile?.roles?.get(0)
        assertThat(role?.name).isEqualTo("ADMIN")
        assertThat(role?.permissions).hasSize(3)
        assertThat(role?.permissions?.map { it.action }).containsExactlyInAnyOrder("READ", "WRITE", "DELETE")
    }

    @Test
    @DisplayName("EXISTS 검증: 일반 유저는 ARTICLE DELETE 권한이 없어야 한다")
    fun testCheckPermission_NormalUser() {
        val canRead = authService.checkPermission(normalUserId, "ARTICLE", "READ")
        val canDelete = authService.checkPermission(normalUserId, "ARTICLE", "DELETE")

        assertThat(canRead).isTrue
        assertThat(canDelete).isFalse
    }

    @Test
    @DisplayName("EXISTS 검증: 관리자는 ARTICLE DELETE 권한이 있어야 한다")
    fun testCheckPermission_AdminUser() {
        val canDelete = authService.checkPermission(adminUserId, "ARTICLE", "DELETE")

        assertThat(canDelete).isTrue
    }
}
