# Chapter 17: 배치 - 실습 개발 플랜

## 1. yml 설정 확인
- `spring.datasource.url` 끝에 `?reWriteBatchedInserts=true` 속성 추가 반영

## 2. 구현 컴포넌트 (`BatchRepository`)
- `void insertSingleBySingle(List<AuthorDto> authors)` 
  - for문 돌면서 `.execute()`
- `void insertInBatch(List<AuthorDto> authors)`
  - Dto를 `AuthorRecord`로 매핑하고 `dsl.batchInsert(records).execute()`

## 3. BDD Test (`BatchTest`)
- 1,000명의 Dummy 저자 생성 (ID 충돌 방지 위해 10,000대 ID 사용)
- `insertSingleBySingle()` 시간 측정 (ms)
- `insertInBatch()` 시간 측정 (ms)
- `assertThat(batchTime).isLessThan(singleTime)` 어설션, 로그 출력
