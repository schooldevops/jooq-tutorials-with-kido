# Project 02: Step-by-Step Implementation Guide

이 문서는 주문/재고 관리 시스템 (Order / Inventory System)의 구현 순서를 상세히 안내합니다.

## Step 1: 프로젝트 구조 초기화
- `00.basic-code-base-skill.md`의 구조(혹은 이전 프로젝트의 베이스)를 기반으로 `code-java`와 `code-kotlin` 모듈을 생성합니다.
- `project-02-order-inventory-system` 폴더로 설정하고 `settings.gradle`을 업데이트합니다.
- `build.gradle`에 Flyway, jOOQ 관련 플러그인을 주입합니다. DB는 `jooq_order`라는 새 DB를 사용합니다.

## Step 2: Database 연동 및 Migration
- **DB 생성 로직**: `CreateDbTest`를 통해 `jooq_order`를 생성하거나 psql 환경을 준비합니다.
- `V1__init_order_inventory.sql` 스크립트 작성 및 실행:
    - `product`, `inventory`, `order_history` 테이블 생성.
- jOOQ Generator (`generateJooq` task)를 실행하여 Java/Kotlin Classes를 자동 생성합니다.

## Step 3: DTO 구축
Java 버전에선 `record`를, Kotlin 버전에선 `data class`를 사용해 객체를 구성합니다.
- `OrderRequestDto`: 사용자 ID, 상품 ID, 요청 수량
- `OrderResponseDto`: 완료된 주문 ID, 총 가격, 성공 여부

## Step 4: Repository (데이터 접근) 로직 구현
### `InventoryRepository`
- `getInventoryForUpdate(Long productId)`: `select().from(INVENTORY).where(INVENTORY.PRODUCT_ID.eq(...)).forUpdate().fetchOptional(...)`
- `deductInventory(Long productId, Integer quantity)`: `update(INVENTORY).set(INVENTORY.QUANTITY, INVENTORY.QUANTITY.minus(quantity))...`

### `OrderRepository`
- `saveOrder(Long userId, Long productId, int quantity, BigDecimal totalPrice)`: 주문 기록 INSERT.

## Step 5: Service 계층 및 트랜잭션 구현
- `OrderService.placeOrder()` 작성 (`@Transactional` 적극 활용).

## Step 6: 통합 테스트 및 동시성 검증
- 초기에 100개의 재고가 있는 상품을 준비합니다.
- `CompletableFuture` 나 `ExecutorService`를 통해 10명의 사용자가 동시에 10개씩 주문하는 동시성 상황을 연출합니다.
- 병렬 테스트 종료 후 `Inventory` 조회 결과 0개인지, 예외 없는지 확인어 검증합니다.
