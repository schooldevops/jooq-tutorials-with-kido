package com.example.demo.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.OrderRequestDto;
import com.example.demo.dto.OrderResponseDto;
import com.example.demo.repository.InventoryRepository;
import com.example.demo.repository.OrderRepository;
import com.example.jooq.tables.records.InventoryRecord;
import com.example.jooq.tables.records.OrderHistoryRecord;

@Service
public class OrderService {

    private final InventoryRepository inventoryRepository;
    private final OrderRepository orderRepository;

    public OrderService(InventoryRepository inventoryRepository, OrderRepository orderRepository) {
        this.inventoryRepository = inventoryRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public OrderResponseDto placeOrder(OrderRequestDto request) {
        InventoryRecord inventory = inventoryRepository.getInventoryForUpdate(request.productId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (inventory.getQuantity() < request.quantity()) {
            throw new RuntimeException("Out of stock");
        }

        inventoryRepository.deductInventory(request.productId(), request.quantity());

        BigDecimal totalPrice = BigDecimal.valueOf(100.0).multiply(BigDecimal.valueOf(request.quantity()));
        
        OrderHistoryRecord order = orderRepository.saveOrder(
                request.userId(),
                request.productId(),
                request.quantity(),
                totalPrice
        );

        return new OrderResponseDto(
                order.getId() != null ? order.getId().longValue() : null,
                "Product-" + request.productId(),
                order.getQuantity(),
                order.getTotalPrice(),
                true,
                "Order placed successfully"
        );
    }
}
