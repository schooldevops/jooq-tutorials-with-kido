package com.example.demo.service

import com.example.demo.dto.OrderRequestDto
import com.example.demo.dto.OrderResponseDto
import com.example.demo.repository.InventoryRepository
import com.example.demo.repository.OrderRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class OrderService(
    private val inventoryRepository: InventoryRepository,
    private val orderRepository: OrderRepository
) {

    @Transactional
    fun placeOrder(request: OrderRequestDto): OrderResponseDto {
        val inventory = inventoryRepository.getInventoryForUpdate(request.productId)
            ?: throw RuntimeException("Product not found")

        if (inventory.quantity < request.quantity) {
            throw RuntimeException("Out of stock")
        }

        inventoryRepository.deductInventory(request.productId, request.quantity)

        val totalPrice = BigDecimal.valueOf(100.0).multiply(BigDecimal.valueOf(request.quantity.toLong()))

        val order = orderRepository.saveOrder(
            request.userId,
            request.productId,
            request.quantity,
            totalPrice
        ) ?: throw RuntimeException("Failed to save order")

        return OrderResponseDto(
            orderId = order.id?.toLong(),
            productName = "Product-${request.productId}",
            quantity = order.quantity!!,
            totalPrice = order.totalPrice,
            success = true,
            message = "Order placed successfully"
        )
    }
}
