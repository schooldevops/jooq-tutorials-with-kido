package com.example.demo.service;

import com.example.demo.dto.DashboardResponseDto;
import com.example.demo.dto.ProductSalesRankDto;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.example.jooq.Tables.DAILY_SALES;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class DashboardServiceTest {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private DSLContext dsl;

    @BeforeEach
    public void setUp() {
        dsl.deleteFrom(DAILY_SALES).execute();

        LocalDate yesterday = LocalDate.of(2023, 10, 1);
        LocalDate today = LocalDate.of(2023, 10, 2);

        // Product A: 100 yesterday, 150 today
        insertSales(yesterday, 1L, "Electronics", "100.00");
        insertSales(today, 1L, "Electronics", "150.00");

        // Product B: 200 yesterday, 120 today (Rank should be behind Product A today)
        insertSales(yesterday, 2L, "Electronics", "200.00");
        insertSales(today, 2L, "Electronics", "120.00");

        // Product C: No sales yesterday, 300 today (Rank 1 in Electronics today)
        insertSales(today, 3L, "Electronics", "300.00");

        // Product D: Different category "Books"
        insertSales(today, 4L, "Books", "50.00");
    }

    private void insertSales(LocalDate date, Long productId, String category, String revenue) {
        dsl.insertInto(DAILY_SALES, DAILY_SALES.SALE_DATE, DAILY_SALES.PRODUCT_ID, DAILY_SALES.CATEGORY, DAILY_SALES.REVENUE)
           .values(date, productId, category, new BigDecimal(revenue))
           .execute();
    }

    @Test
    public void testDailyDashboard() {
        LocalDate targetDate = LocalDate.of(2023, 10, 2);
        DashboardResponseDto response = dashboardService.getDailyDashboard(targetDate);

        assertThat(response.targetDate()).isEqualTo(targetDate);
        assertThat(response.salesRanks()).hasSize(4);

        // Product C should be Rank 1 in Electronics, revenue 300, previous null
        ProductSalesRankDto productC = response.salesRanks().stream().filter(r -> r.productId() == 3L).findFirst().get();
        assertThat(productC.rank()).isEqualTo(1);
        assertThat(productC.revenue()).isEqualByComparingTo("300.00");
        assertThat(productC.previousRevenue()).isNull();

        // Product A should be Rank 2, revenue 150, previous 100
        ProductSalesRankDto productA = response.salesRanks().stream().filter(r -> r.productId() == 1L).findFirst().get();
        assertThat(productA.rank()).isEqualTo(2);
        assertThat(productA.previousRevenue()).isEqualByComparingTo("100.00");

        // Product B should be Rank 3, revenue 120, previous 200
        ProductSalesRankDto productB = response.salesRanks().stream().filter(r -> r.productId() == 2L).findFirst().get();
        assertThat(productB.rank()).isEqualTo(3);
        assertThat(productB.previousRevenue()).isEqualByComparingTo("200.00");

        // Product D should be Rank 1 in Books
        ProductSalesRankDto productD = response.salesRanks().stream().filter(r -> r.productId() == 4L).findFirst().get();
        assertThat(productD.rank()).isEqualTo(1);
        assertThat(productD.category()).isEqualTo("Books");
    }
}
