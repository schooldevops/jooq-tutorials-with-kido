package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import com.example.demo.dto.ProductSalesRankDto;
import static com.example.jooq.Tables.DAILY_SALES;

@Repository
public class DashboardRepository {

    private final DSLContext dsl;

    public DashboardRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public List<ProductSalesRankDto> getDashboardData(LocalDate targetDate) {
        var cte = dsl.select(
                        DAILY_SALES.SALE_DATE.as("saleDate"),
                        DAILY_SALES.PRODUCT_ID.as("productId"),
                        DAILY_SALES.CATEGORY.as("category"),
                        DAILY_SALES.REVENUE.as("revenue"),
                        DSL.lag(DAILY_SALES.REVENUE)
                           .over()
                           .partitionBy(DAILY_SALES.PRODUCT_ID)
                           .orderBy(DAILY_SALES.SALE_DATE)
                           .as("previousRevenue"),
                        DSL.rank()
                           .over()
                           .partitionBy(DAILY_SALES.SALE_DATE, DAILY_SALES.CATEGORY)
                           .orderBy(DAILY_SALES.REVENUE.desc())
                           .as("rank")
                )
                .from(DAILY_SALES)
                .where(DAILY_SALES.SALE_DATE.between(targetDate.minusDays(1), targetDate))
                .asTable("SalesCte");

        return dsl.select(
                    cte.field("saleDate"),
                    cte.field("productId"),
                    cte.field("category"),
                    cte.field("revenue"),
                    cte.field("previousRevenue"),
                    cte.field("rank")
                )
                .from(cte)
                .where(cte.field("saleDate", LocalDate.class).eq(targetDate))
                .fetchInto(ProductSalesRankDto.class);
    }
}
