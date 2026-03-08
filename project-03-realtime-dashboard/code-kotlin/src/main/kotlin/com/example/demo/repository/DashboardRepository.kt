package com.example.demo.repository

import com.example.demo.dto.ProductSalesRankDto
import com.example.jooq.Tables.DAILY_SALES
import org.jooq.DSLContext
import org.jooq.impl.DSL.lag
import org.jooq.impl.DSL.rank
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class DashboardRepository(
    private val dsl: DSLContext
) {
    fun getDashboardData(targetDate: LocalDate): List<ProductSalesRankDto> {
        val cteFields = listOf(
            DAILY_SALES.SALE_DATE.`as`("saleDate"),
            DAILY_SALES.PRODUCT_ID.`as`("productId"),
            DAILY_SALES.CATEGORY.`as`("category"),
            DAILY_SALES.REVENUE.`as`("revenue"),
            lag(DAILY_SALES.REVENUE)
                .over()
                .partitionBy(DAILY_SALES.PRODUCT_ID)
                .orderBy(DAILY_SALES.SALE_DATE)
                .`as`("previousRevenue"),
            rank()
                .over()
                .partitionBy(DAILY_SALES.SALE_DATE, DAILY_SALES.CATEGORY)
                .orderBy(DAILY_SALES.REVENUE.desc())
                .`as`("rank")
        )
        val cte = dsl.select(cteFields)
            .from(DAILY_SALES)
            .where(DAILY_SALES.SALE_DATE.between(targetDate.minusDays(1), targetDate))
            .asTable("SalesCte")

        val resultFields = listOf(
            cte.field("saleDate"),
            cte.field("productId"),
            cte.field("category"),
            cte.field("revenue"),
            cte.field("previousRevenue"),
            cte.field("rank")
        )
        return dsl.select(resultFields)
            .from(cte)
            .where(cte.field("saleDate", LocalDate::class.java)?.eq(targetDate))
            .fetchInto(ProductSalesRankDto::class.java)
    }
}
