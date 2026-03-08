package com.example.demo.repository;

import java.util.Optional;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static com.example.jooq.tables.Inventory.INVENTORY;
import com.example.jooq.tables.records.InventoryRecord;

@Repository
public class InventoryRepository {

    private final DSLContext dsl;

    public InventoryRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Optional<InventoryRecord> getInventoryForUpdate(Long productId) {
        return dsl.selectFrom(INVENTORY)
                .where(INVENTORY.PRODUCT_ID.eq(productId))
                .forUpdate()
                .fetchOptional();
    }

    public int deductInventory(Long productId, int quantity) {
        return dsl.update(INVENTORY)
                .set(INVENTORY.QUANTITY, INVENTORY.QUANTITY.minus(quantity))
                .where(INVENTORY.PRODUCT_ID.eq(productId))
                .execute();
    }
}
