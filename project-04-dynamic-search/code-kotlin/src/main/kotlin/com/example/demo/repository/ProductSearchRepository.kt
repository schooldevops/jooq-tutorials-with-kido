package com.example.demo.repository

import com.example.demo.dto.ProductDto
import com.example.demo.dto.ProductSearchRequestDto
import com.example.jooq.tables.references.PRODUCT
import org.jooq.Condition
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class ProductSearchRepository(
    private val dsl: DSLContext
) {

    fun searchProducts(request: ProductSearchRequestDto): List<ProductDto> {
        val conditions = buildSearchConditions(request)
        
        return dsl.selectFrom(PRODUCT)
            .where(conditions) // jOOQ where can accept Collection<Condition>
            .fetchInto(ProductDto::class.java)
    }

    private fun buildSearchConditions(request: ProductSearchRequestDto): Collection<Condition> {
        val conditions = mutableListOf<Condition>()

        request.keyword?.takeIf { it.isNotBlank() }?.let { 
            conditions.add(PRODUCT.NAME.containsIgnoreCase(it)) 
        }
        request.category?.takeIf { it.isNotBlank() }?.let { 
            conditions.add(PRODUCT.CATEGORY.eq(it)) 
        }
        request.minPrice?.let { 
            conditions.add(PRODUCT.PRICE.ge(it)) 
        }
        request.maxPrice?.let { 
            conditions.add(PRODUCT.PRICE.le(it)) 
        }
        request.stockStatus?.takeIf { it.isNotBlank() }?.let { 
            conditions.add(PRODUCT.STOCK_STATUS.eq(it)) 
        }
        request.status?.takeIf { it.isNotBlank() }?.let { 
            conditions.add(PRODUCT.STATUS.eq(it)) 
        }
        request.manufacturer?.takeIf { it.isNotBlank() }?.let { 
            conditions.add(PRODUCT.MANUFACTURER.eq(it)) 
        }

        return conditions
    }
}
