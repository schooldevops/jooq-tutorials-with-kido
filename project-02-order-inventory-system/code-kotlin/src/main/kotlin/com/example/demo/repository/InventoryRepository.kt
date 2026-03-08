package com.example.demo.repository

import com.example.jooq.tables.Inventory.INVENTORY
import com.example.jooq.tables.records.InventoryRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class InventoryRepository(private val dsl: DSLContext) {

    fun getInventoryForUpdate(productId: Long): InventoryRecord? {
        return dsl.selectFrom(INVENTORY)
            .where(INVENTORY.PRODUCT_ID.eq(productId))
            .forUpdate()
            .fetchOne()
    }

    fun deductInventory(productId: Long, quantity: Int): Int {
        return dsl.update(INVENTORY)
            .set(INVENTORY.QUANTITY, INVENTORY.QUANTITY.minus(quantity))
            .where(INVENTORY.PRODUCT_ID.eq(productId))
            .execute()
    }
}
