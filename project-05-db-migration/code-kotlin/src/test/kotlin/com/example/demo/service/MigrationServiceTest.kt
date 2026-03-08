package com.example.demo.service

import com.example.jooq.legacy.tables.references.ORDERS_V1
import com.example.jooq.legacy.tables.references.USERS_V1
import com.example.jooq.target.tables.references.MEMBER
import com.example.jooq.target.tables.references.PURCHASE_ORDER
import org.assertj.core.api.Assertions.assertThat
import org.jooq.DSLContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import java.time.LocalDateTime

@SpringBootTest
class MigrationServiceTest(
    @Autowired private val migrationService: MigrationService,
    @Autowired @Qualifier("legacyDsl") private val legacyDsl: DSLContext,
    @Autowired @Qualifier("targetDsl") private val targetDsl: DSLContext
) {

    @BeforeEach
    fun setUp() {
        // Clear
        legacyDsl.truncate(ORDERS_V1).cascade().execute()
        legacyDsl.truncate(USERS_V1).cascade().execute()
        
        targetDsl.truncate(PURCHASE_ORDER).cascade().execute()
        targetDsl.truncate(MEMBER).cascade().execute()

        // Generate 100 Legacy Users
        val legacyUsers = (1..100).map { i ->
            legacyDsl.newRecord(USERS_V1).apply {
                id = i.toLong()
                fullName = "KotlinUser FirstName$i LastName$i"
                userStatus = if (i % 2 == 0) "VALID" else "REVOKED"
                createdDate = LocalDateTime.now().minusDays(i.toLong())
            }
        }
        legacyDsl.batchInsert(legacyUsers).execute()

        // Generate 100 Legacy Orders
        val legacyOrders = (1..100).map { i ->
            legacyDsl.newRecord(ORDERS_V1).apply {
                id = i.toLong()
                userId = i.toLong()
                orderTotal = BigDecimal("10.50").multiply(BigDecimal(i))
                state = if (i % 3 == 0) "PAID" else "FAIL"
            }
        }
        legacyDsl.batchInsert(legacyOrders).execute()
    }

    @Test
    @DisplayName("Should successfully migrate all users and orders with correct type mappings")
    fun testMigrateAll() {
        // When
        migrationService.migrateAll()

        // Then: Members
        val members = targetDsl.selectFrom(MEMBER).fetch()
        assertThat(members).hasSize(100)

        val member2 = members.first { it.id == 2L }
        assertThat(member2.firstName).isEqualTo("KotlinUser")
        assertThat(member2.lastName).isEqualTo("FirstName2 LastName2")
        assertThat(member2.status).isEqualTo("ACTIVE")

        val member3 = members.first { it.id == 3L }
        assertThat(member3.status).isEqualTo("INACTIVE")

        // Then: Purchase Orders
        val pos = targetDsl.selectFrom(PURCHASE_ORDER).fetch()
        assertThat(pos).hasSize(100)

        val po3 = pos.first { it.id == 3L }
        assertThat(po3.memberId).isEqualTo(3L)
        assertThat(po3.orderState).isEqualTo("COMPLETED")

        val po4 = pos.first { it.id == 4L }
        assertThat(po4.orderState).isEqualTo("PENDING")
    }
}
