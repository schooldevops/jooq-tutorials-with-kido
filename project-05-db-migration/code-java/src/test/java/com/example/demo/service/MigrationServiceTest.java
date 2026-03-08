package com.example.demo.service;

import com.example.jooq.legacy.tables.records.OrdersV1Record;
import com.example.jooq.legacy.tables.records.UsersV1Record;
import com.example.jooq.target.tables.records.MemberRecord;
import com.example.jooq.target.tables.records.PurchaseOrderRecord;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.jooq.legacy.Tables.ORDERS_V1;
import static com.example.jooq.legacy.Tables.USERS_V1;
import static com.example.jooq.target.Tables.MEMBER;
import static com.example.jooq.target.Tables.PURCHASE_ORDER;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MigrationServiceTest {

    @Autowired
    private MigrationService migrationService;

    @Autowired
    @Qualifier("legacyDsl")
    private DSLContext legacyDsl;

    @Autowired
    @Qualifier("targetDsl")
    private DSLContext targetDsl;

    @BeforeEach
    void setUp() {
        // Clear both databases
        legacyDsl.truncate(ORDERS_V1).cascade().execute();
        legacyDsl.truncate(USERS_V1).cascade().execute();
        
        targetDsl.truncate(PURCHASE_ORDER).cascade().execute();
        targetDsl.truncate(MEMBER).cascade().execute();

        // 1. Generate 100 Legacy Users
        List<UsersV1Record> legacyUsers = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            UsersV1Record user = legacyDsl.newRecord(USERS_V1);
            user.setId((long) i);
            user.setFullName("User FirstName" + i + " LastName" + i);
            user.setUserStatus(i % 2 == 0 ? "VALID" : "REVOKED"); // Even: VALID (ACTIVE), Odd: REVOKED (INACTIVE)
            user.setCreatedDate(LocalDateTime.now().minusDays(i));
            legacyUsers.add(user);
        }
        legacyDsl.batchInsert(legacyUsers).execute();

        // 2. Generate 100 Legacy Orders
        List<OrdersV1Record> legacyOrders = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            OrdersV1Record order = legacyDsl.newRecord(ORDERS_V1);
            order.setId((long) i);
            order.setUserId((long) i);
            order.setOrderTotal(new BigDecimal("10.50").multiply(new BigDecimal(i)));
            order.setState(i % 3 == 0 ? "PAID" : "FAIL"); // PAID -> COMPLETED, FAIL -> PENDING
            legacyOrders.add(order);
        }
        legacyDsl.batchInsert(legacyOrders).execute();
    }

    @Test
    @DisplayName("Should successfully migrate all users and orders with correct type mappings")
    void testMigrateAll() {
        // When
        migrationService.migrateAll();

        // Then: verify Members
        List<MemberRecord> members = targetDsl.selectFrom(MEMBER).fetch();
        assertThat(members).hasSize(100);

        MemberRecord member2 = members.stream().filter(m -> m.getId() == 2L).findFirst().orElseThrow();
        assertThat(member2.getFirstName()).isEqualTo("User");
        assertThat(member2.getLastName()).isEqualTo("FirstName2 LastName2");
        assertThat(member2.getStatus()).isEqualTo("ACTIVE"); // mapped from VALID

        MemberRecord member3 = members.stream().filter(m -> m.getId() == 3L).findFirst().orElseThrow();
        assertThat(member3.getStatus()).isEqualTo("INACTIVE"); // mapped from REVOKED

        // Then: verify Purchase Orders
        List<PurchaseOrderRecord> pos = targetDsl.selectFrom(PURCHASE_ORDER).fetch();
        assertThat(pos).hasSize(100);

        PurchaseOrderRecord po3 = pos.stream().filter(p -> p.getId() == 3L).findFirst().orElseThrow();
        assertThat(po3.getMemberId()).isEqualTo(3L);
        assertThat(po3.getOrderState()).isEqualTo("COMPLETED"); // mapped from PAID

        PurchaseOrderRecord po4 = pos.stream().filter(p -> p.getId() == 4L).findFirst().orElseThrow();
        assertThat(po4.getOrderState()).isEqualTo("PENDING"); // mapped from FAIL
    }
}
