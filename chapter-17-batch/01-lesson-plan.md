# Chapter 17: 배치(Batch) 처리와 성능

## 1. 학습 목표
1. **단건 Insert의 성능 한계 체험:** `forEach`로 하나씩 Insert 할 때의 네트워크 왕복(I/O) 오버헤드 이해
2. **jOOQ `batchInsert()` 사용법:** 대량의 데이터를 매끄럽게 DB로 밀어넣는 배치 API 학습
3. **JDBC reWriteBatchedInserts:** PostgreSQL 등에서 JDBC 드라이버 차원의 배치 최적화를 켜는 방법

## 2. 핵심 jOOQ API
- `dsl.batchInsert(records).execute()` - 여러 레코드를 한 번의 배치 I/O로 인서트
- `dsl.batchUpdate(records).execute()` - 여러 레코드의 업데이트를 동일하게 캐싱/배치 처리
- `dsl.batch(queries).execute()` - 다양한 형태의 혼합된 쿼리들을 한 번에 실행

## 3. 실습 시나리오
- Author 1,000건 삽입 테스트
- 1. `insertSingleBySingle`: `dsl.insertInto(...).execute()` x 1000
- 2. `insertInBatch`: `dsl.batchInsert(...)` 
- 테스트 코드로 두 메서드의 실행 시간(ms)을 비교하여 배치 인서트가 더 빠르다는 점을 검증(Assert)
