package com.example.demo.service;

import com.example.jooq.legacy.tables.records.OrdersV1Record;
import com.example.jooq.legacy.tables.records.UsersV1Record;
import com.example.jooq.target.tables.records.MemberRecord;
import com.example.jooq.target.tables.records.PurchaseOrderRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.jooq.legacy.Tables.ORDERS_V1;
import static com.example.jooq.legacy.Tables.USERS_V1;
import static com.example.jooq.target.Tables.MEMBER;
import static com.example.jooq.target.Tables.PURCHASE_ORDER;

@Service
public class MigrationService {

    private final DSLContext legacyDsl;
    private final DSLContext targetDsl;

    public MigrationService(@Qualifier("legacyDsl") DSLContext legacyDsl,
                            @Qualifier("targetDsl") DSLContext targetDsl) {
        this.legacyDsl = legacyDsl;
        this.targetDsl = targetDsl;
    }

    public void migrateAll() {
        migrateUsersToMembers();
        migrateOrdersToPurchaseOrders();
    }

    private void migrateUsersToMembers() {
        List<UsersV1Record> legacyUsers = legacyDsl.selectFrom(USERS_V1).fetch();

        List<MemberRecord> memberRecords = legacyUsers.stream()
                .map(user -> {
                    MemberRecord member = targetDsl.newRecord(MEMBER);
                    member.setId(user.getId()); // Keep exact ID

                    // Name splitting
                    String[] nameParts = user.getFullName().split(" ", 2);
                    member.setFirstName(nameParts[0]);
                    member.setLastName(nameParts.length > 1 ? nameParts[1] : "");

                    // Status mapping
                    String status = processStatus(user.getUserStatus());
                    member.setStatus(status);
                    
                    LocalDateTime createdAt = user.getCreatedDate();
                    member.setCreatedAt(createdAt);
                    
                    return member;
                }).collect(Collectors.toList());

        // Batch Insert with high performance
        targetDsl.batchInsert(memberRecords).execute();
    }

    private void migrateOrdersToPurchaseOrders() {
        List<OrdersV1Record> legacyOrders = legacyDsl.selectFrom(ORDERS_V1).fetch();

        List<PurchaseOrderRecord> poRecords = legacyOrders.stream()
                .map(order -> {
                    PurchaseOrderRecord po = targetDsl.newRecord(PURCHASE_ORDER);
                    po.setId(order.getId());
                    po.setMemberId(order.getUserId());
                    po.setTotalAmount(order.getOrderTotal());
                    po.setOrderState(processOrderState(order.getState()));
                    return po;
                }).collect(Collectors.toList());

        targetDsl.batchInsert(poRecords).execute();
    }

    private String processStatus(String legacyStatus) {
        if ("ACTIVE".equalsIgnoreCase(legacyStatus) || "VALID".equalsIgnoreCase(legacyStatus)) {
            return "ACTIVE";
        }
        return "INACTIVE";
    }

    private String processOrderState(String legacyState) {
        if ("PAID".equalsIgnoreCase(legacyState) || "SUCCESS".equalsIgnoreCase(legacyState)) {
            return "COMPLETED";
        }
        return "PENDING";
    }
}
