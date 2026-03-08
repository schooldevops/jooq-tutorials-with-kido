package com.example.demo.repository;

import java.math.BigDecimal;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static com.example.jooq.tables.OrderHistory.ORDER_HISTORY;
import com.example.jooq.tables.records.OrderHistoryRecord;

@Repository
public class OrderRepository {

    private final DSLContext dsl;

    public OrderRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public OrderHistoryRecord saveOrder(Long userId, Long productId, int quantity, BigDecimal totalPrice) {
        return dsl.insertInto(ORDER_HISTORY, 
                ORDER_HISTORY.USER_ID, 
                ORDER_HISTORY.PRODUCT_ID, 
                ORDER_HISTORY.QUANTITY, 
                ORDER_HISTORY.TOTAL_PRICE)
                .values(userId, productId, quantity, totalPrice)
                .returning()
                .fetchOne();
    }
}
