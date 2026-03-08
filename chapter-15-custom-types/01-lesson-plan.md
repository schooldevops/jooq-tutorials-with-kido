# Chapter 15: 커스텀 데이터 타입과 컨버터

## 1. 학습 목표
1. **Enum 매핑:** Java/Kotlin Enum ↔ DB VARCHAR 변환을 `Converter`로 자동화
2. **Converter 인터페이스:** `from()`/`to()` 메서드로 도메인 타입 ↔ DB 타입 쌍방향 변환
3. **jOOQ DSL에서 Enum 사용:** `.eq()`, `.in()` 등 타입 안전 쿼리 작성

## 2. 핵심 jOOQ API
- `DSL.val(value, converter)` - Enum 값을 DSL에 바인딩
- `field.convert(converter)` - 필드에 컨버터 적용
- `record.get(field, converter)` - 레코드 값을 변환해 읽기
- `Converter<T, U>` - DB 타입(T) ↔ 도메인 타입(U)

## 3. 실습 시나리오
- `BookStatus` Enum(DRAFT/PUBLISHED/ARCHIVED) ↔ DB VARCHAR 매핑
- status 컬럼 기준 필터, 조회, 업데이트
