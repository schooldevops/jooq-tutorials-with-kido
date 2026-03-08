package com.example.demo.repository

import com.example.jooq.tables.OrderHistory.ORDER_HISTORY
import com.example.jooq.tables.records.OrderHistoryRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
class OrderRepository(private val dsl: DSLContext) {

    fun saveOrder(userId: Long, productId: Long, quantity: Int, totalPrice: BigDecimal): OrderHistoryRecord? {
        return dsl.insertInto(ORDER_HISTORY,
            ORDER_HISTORY.USER_ID,
            ORDER_HISTORY.PRODUCT_ID,
            ORDER_HISTORY.QUANTITY,
            ORDER_HISTORY.TOTAL_PRICE)
            .values(userId, productId, quantity, totalPrice)
            .returning()
            .fetchOne()
    }
}
