package com.example.demo.service;

import com.example.demo.dto.OrderRequestDto;
import com.example.jooq.tables.records.InventoryRecord;
import com.example.jooq.tables.records.ProductRecord;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.jooq.tables.Inventory.INVENTORY;
import static com.example.jooq.tables.OrderHistory.ORDER_HISTORY;
import static com.example.jooq.tables.Product.PRODUCT;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class OrderConcurrencyTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private DSLContext dsl;

    private Long testProductId;

    @BeforeEach
    void setUp() {
        dsl.deleteFrom(ORDER_HISTORY).execute();
        dsl.deleteFrom(INVENTORY).execute();
        dsl.deleteFrom(PRODUCT).execute();

        ProductRecord product = dsl.insertInto(PRODUCT, PRODUCT.NAME, PRODUCT.PRICE)
                .values("Test Product", BigDecimal.valueOf(100.0))
                .returning(PRODUCT.ID)
                .fetchOne();
        testProductId = product.getId().longValue();

        dsl.insertInto(INVENTORY, INVENTORY.PRODUCT_ID, INVENTORY.QUANTITY)
                .values(testProductId, 10) // 10 items in stock
                .execute();
    }

    @Test
    @DisplayName("100개의 요청이 동시에 들어올 때 정확히 10개만 주문 성공하고 90개는 실패해야 한다 (재고 0 보장)")
    void testConcurrency() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            long userId = i + 1;
            executorService.submit(() -> {
                try {
                    OrderRequestDto request = new OrderRequestDto(userId, testProductId, 1);
                    orderService.placeOrder(request);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        assertThat(successCount.get()).isEqualTo(10);
        assertThat(failCount.get()).isEqualTo(90);

        InventoryRecord inventory = dsl.selectFrom(INVENTORY)
                .where(INVENTORY.PRODUCT_ID.eq(testProductId))
                .fetchOne();
        assertThat(inventory.getQuantity()).isEqualTo(0);
    }
}
