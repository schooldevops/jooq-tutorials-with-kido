package com.example.demo.service

import com.example.demo.dto.OrderRequestDto
import com.example.jooq.tables.Inventory.INVENTORY
import com.example.jooq.tables.OrderHistory.ORDER_HISTORY
import com.example.jooq.tables.Product.PRODUCT
import org.assertj.core.api.Assertions.assertThat
import org.jooq.DSLContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

@SpringBootTest
class OrderConcurrencyTest @Autowired constructor(
    private val orderService: OrderService,
    private val dsl: DSLContext
) {

    private var testProductId: Long = 0

    @BeforeEach
    fun setUp() {
        dsl.deleteFrom(ORDER_HISTORY).execute()
        dsl.deleteFrom(INVENTORY).execute()
        dsl.deleteFrom(PRODUCT).execute()

        val product = dsl.insertInto(PRODUCT, PRODUCT.NAME, PRODUCT.PRICE)
            .values("Test Product", BigDecimal.valueOf(100.0))
            .returning(PRODUCT.ID)
            .fetchOne()!!
        testProductId = product.id?.toLong()!!

        dsl.insertInto(INVENTORY, INVENTORY.PRODUCT_ID, INVENTORY.QUANTITY)
            .values(testProductId, 10)
            .execute()
    }

    @Test
    @DisplayName("100개의 요청이 동시에 들어올 때 정확히 10개만 주문 성공하고 90개는 실패해야 한다 (재고 0 보장)")
    fun testConcurrency() {
        val threadCount = 100
        val executorService = Executors.newFixedThreadPool(32)
        val latch = CountDownLatch(threadCount)

        val successCount = AtomicInteger(0)
        val failCount = AtomicInteger(0)

        for (i in 0 until threadCount) {
            val userId = (i + 1).toLong()
            executorService.submit {
                try {
                    val request = OrderRequestDto(userId, testProductId, 1)
                    orderService.placeOrder(request)
                    successCount.incrementAndGet()
                } catch (e: Exception) {
                    failCount.incrementAndGet()
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()
        executorService.shutdown()

        assertThat(successCount.get()).isEqualTo(10)
        assertThat(failCount.get()).isEqualTo(90)

        val inventory = dsl.selectFrom(INVENTORY)
            .where(INVENTORY.PRODUCT_ID.eq(testProductId))
            .fetchOne()!!
        assertThat(inventory.quantity).isEqualTo(0)
    }
}
