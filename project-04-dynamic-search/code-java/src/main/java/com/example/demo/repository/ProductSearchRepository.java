package com.example.demo.repository;

import java.util.List;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.example.demo.dto.ProductDto;
import com.example.demo.dto.ProductSearchRequestDto;
import static com.example.jooq.tables.Product.PRODUCT;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductSearchRepository {

    private final DSLContext dsl;

    public List<ProductDto> searchProducts(ProductSearchRequestDto request) {
        return dsl.selectFrom(PRODUCT)
                .where(buildSearchConditions(request))
                .fetchInto(ProductDto.class);
    }

    private Condition buildSearchConditions(ProductSearchRequestDto request) {
        Condition condition = DSL.noCondition(); // DSL.noCondition() acts as a true/no-op condition

        if (StringUtils.hasText(request.getKeyword())) {
            condition = condition.and(PRODUCT.NAME.containsIgnoreCase(request.getKeyword()));
        }
        if (StringUtils.hasText(request.getCategory())) {
            condition = condition.and(PRODUCT.CATEGORY.eq(request.getCategory()));
        }
        if (request.getMinPrice() != null) {
            condition = condition.and(PRODUCT.PRICE.ge(request.getMinPrice()));
        }
        if (request.getMaxPrice() != null) {
            condition = condition.and(PRODUCT.PRICE.le(request.getMaxPrice()));
        }
        if (StringUtils.hasText(request.getStockStatus())) {
            condition = condition.and(PRODUCT.STOCK_STATUS.eq(request.getStockStatus()));
        }
        if (StringUtils.hasText(request.getStatus())) {
            condition = condition.and(PRODUCT.STATUS.eq(request.getStatus()));
        }
        if (StringUtils.hasText(request.getManufacturer())) {
            condition = condition.and(PRODUCT.MANUFACTURER.eq(request.getManufacturer()));
        }

        return condition;
    }
}
