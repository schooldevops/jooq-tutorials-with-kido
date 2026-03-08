# Project 02: Order/Inventory Management System Architecture

## 1. Overview
이 프로젝트는 실무에서 자주 직면하는 핵심 과제인 **"재고 차감 시의 동시성 제어(Concurrency Control) 및 트랜잭션 보장"**을 jOOQ로 어떻게 우아하게 처리할 수 있는지 실습하는 프로젝트입니다.

## 2. DB Schema Design
주문과 재고 관리를 위해 3개의 주요 테이블을 정의합니다.

### 2.1. `product` 테이블
상품의 기본 정보를 저장합니다.
- `id` (PK, Serial)
- `name` (Varchar, 상품명)
- `price` (Decimal, 가격)

### 2.2. `inventory` 테이블
상품별 현재고를 저장하며, 다수의 요청이 동시에 업데이트를 시도하는 대상입니다.
- `product_id` (PK, FK to `product(id)`)
- `quantity` (Integer, 현재고)
- `updated_at` (Timestamp)

### 2.3. `order_history` 테이블
사용자가 체결한 주문 내역을 저장합니다.
- `id` (PK, Serial)
- `user_id` (Long, 임의 주입용)
- `product_id` (FK to `product(id)`)
- `quantity` (Integer, 주문 수량)
- `total_price` (Decimal, 총 결제 금액)
- `created_at` (Timestamp)

## 3. Key Concepts (jOOQ Features)

### 3.1. `selectForUpdate()` (Pessimistic Locking)
jOOQ 레벨에서 SQL의 `SELECT ... FOR UPDATE`를 직관적으로 호출하여 트랜잭션 진행 중 재고 데이터에 대한 Row-level 배타락(Exclusive Lock)을 획득합니다. 이를 통해 초과 주문(Over-selling) 문제를 방지합니다.

### 3.2. Spring `@Transactional` 과의 통합
트랜잭션 바운더리 내에서 jOOQ의 코드가 실행될 때, 스프링 트랜잭션 매니저와 올바르게 DataSource/Connection을 공유하는 방식을 테스트합니다.

## 4. Flow of "Place Order"
1. `OrderService.placeOrder(userId, productId, orderQuantity)` 호출 (`@Transactional` 적용)
2. `InventoryRepository.getInventoryForUpdate(productId)` 를 호출하여 재고 락(Lock) 획득 및 수량 확인.
3. 재고 부족 시 `OutOfStockException` 발생 (Rollback 됨).
4. 재고가 충분할 경우 `InventoryRepository.deductInventory(productId, orderQuantity)`로 수량 차감 업데이트.
5. `OrderRepository.saveOrder(...)` 를 통해 `order_history` 테이블에 내역 저장.
6. 커밋 완료로 트랜잭션 종료, Lock 해제.
