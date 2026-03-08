package com.example.demo.service

import com.example.jooq.Tables.DAILY_SALES
import org.assertj.core.api.Assertions.assertThat
import org.jooq.DSLContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import java.time.LocalDate

@SpringBootTest
class DashboardServiceTest {

    @Autowired
    private lateinit var dashboardService: DashboardService

    @Autowired
    private lateinit var dsl: DSLContext

    @BeforeEach
    fun setUp() {
        dsl.deleteFrom(DAILY_SALES).execute()

        val yesterday = LocalDate.of(2023, 10, 1)
        val today = LocalDate.of(2023, 10, 2)

        // Product A: 100 yesterday, 150 today
        insertSales(yesterday, 1L, "Electronics", "100.00")
        insertSales(today, 1L, "Electronics", "150.00")

        // Product B: 200 yesterday, 120 today (Rank should be behind Product A today)
        insertSales(yesterday, 2L, "Electronics", "200.00")
        insertSales(today, 2L, "Electronics", "120.00")

        // Product C: No sales yesterday, 300 today (Rank 1 in Electronics today)
        insertSales(today, 3L, "Electronics", "300.00")

        // Product D: Different category "Books"
        insertSales(today, 4L, "Books", "50.00")
    }

    private fun insertSales(date: LocalDate, productId: Long, category: String, revenue: String) {
        dsl.insertInto(DAILY_SALES, DAILY_SALES.SALE_DATE, DAILY_SALES.PRODUCT_ID, DAILY_SALES.CATEGORY, DAILY_SALES.REVENUE)
            .values(date, productId, category, BigDecimal(revenue))
            .execute()
    }

    @Test
    fun testDailyDashboard() {
        val targetDate = LocalDate.of(2023, 10, 2)
        val response = dashboardService.getDailyDashboard(targetDate)

        assertThat(response.targetDate).isEqualTo(targetDate)
        assertThat(response.salesRanks).hasSize(4)

        // Product C should be Rank 1 in Electronics, revenue 300, previous null
        val productC = response.salesRanks.first { it.productId == 3L }
        assertThat(productC.rank).isEqualTo(1)
        assertThat(productC.revenue).isEqualByComparingTo("300.00")
        assertThat(productC.previousRevenue).isNull()

        // Product A should be Rank 2, revenue 150, previous 100
        val productA = response.salesRanks.first { it.productId == 1L }
        assertThat(productA.rank).isEqualTo(2)
        assertThat(productA.previousRevenue).isEqualByComparingTo("100.00")

        // Product B should be Rank 3, revenue 120, previous 200
        val productB = response.salesRanks.first { it.productId == 2L }
        assertThat(productB.rank).isEqualTo(3)
        assertThat(productB.previousRevenue).isEqualByComparingTo("200.00")

        // Product D should be Rank 1 in Books
        val productD = response.salesRanks.first { it.productId == 4L }
        assertThat(productD.rank).isEqualTo(1)
        assertThat(productD.category).isEqualTo("Books")
    }
}
