package com.example.demo.service

import com.example.jooq.legacy.tables.references.ORDERS_V1
import com.example.jooq.legacy.tables.references.USERS_V1
import com.example.jooq.target.tables.references.MEMBER
import com.example.jooq.target.tables.references.PURCHASE_ORDER
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class MigrationService(
    @Qualifier("legacyDsl") private val legacyDsl: DSLContext,
    @Qualifier("targetDsl") private val targetDsl: DSLContext
) {

    fun migrateAll() {
        migrateUsersToMembers()
        migrateOrdersToPurchaseOrders()
    }

    private fun migrateUsersToMembers() {
        val legacyUsers = legacyDsl.selectFrom(USERS_V1).fetch()

        val memberRecords = legacyUsers.map { user ->
            val member = targetDsl.newRecord(MEMBER)
            member.id = user.id
            
            // Name splitting
            val nameParts = user.fullName!!.split(" ", limit = 2)
            member.firstName = nameParts[0]
            member.lastName = if (nameParts.size > 1) nameParts[1] else ""
            
            // Status mapping
            member.status = processStatus(user.userStatus)
            member.createdAt = user.createdDate
            
            member
        }

        // Batch Insert
        targetDsl.batchInsert(memberRecords).execute()
    }

    private fun migrateOrdersToPurchaseOrders() {
        val legacyOrders = legacyDsl.selectFrom(ORDERS_V1).fetch()

        val poRecords = legacyOrders.map { order ->
            val po = targetDsl.newRecord(PURCHASE_ORDER)
            po.id = order.id
            po.memberId = order.userId
            po.totalAmount = order.orderTotal
            po.orderState = processOrderState(order.state)
            po
        }

        targetDsl.batchInsert(poRecords).execute()
    }

    private fun processStatus(legacyStatus: String?): String {
        return if (legacyStatus.equals("ACTIVE", ignoreCase = true) || legacyStatus.equals("VALID", ignoreCase = true)) {
            "ACTIVE"
        } else {
            "INACTIVE"
        }
    }

    private fun processOrderState(legacyState: String?): String {
        return if (legacyState.equals("PAID", ignoreCase = true) || legacyState.equals("SUCCESS", ignoreCase = true)) {
            "COMPLETED"
        } else {
            "PENDING"
        }
    }
}
