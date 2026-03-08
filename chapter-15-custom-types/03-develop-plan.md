# Chapter 15: 커스텀 타입과 컨버터 - 실습 개발 플랜

## 구현 메서드

| 메서드 | 기법 | DTO |
|--------|------|-----|
| `findByStatus(BookStatus)` | Enum Converter + WHERE | `BookWithStatus` |
| `findAllWithStatus()` | 전체 조회 + Enum 변환 | `BookWithStatus` |
| `updateBookStatus(id, status)` | Enum → VARCHAR UPDATE | `int` (rows affected) |

## BDD 테스트 시나리오
```
1. findByStatus(PUBLISHED): 모두 status == PUBLISHED
2. findByStatus(DRAFT): status == DRAFT 인 것만 포함
3. findAllWithStatus: 결과에 null status 없음
4. updateBookStatus: 업데이트 후 재조회 시 변경된 status 확인
5. updateBookStatus: 반환값 == 1 (1건 변경)
```
