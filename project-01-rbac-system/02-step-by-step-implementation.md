# 실전 샘플 프로젝트 1: 단계별 구현 가이드 (Step-by-Step)

이 문서는 실무에서 사용할 수 있는 RBAC 시스템을 jOOQ로 구현하기 위한 개발 가이드입니다. Java (Spring Boot) 버전을 기준으로 설명하지만, 로직 흐름은 Kotlin에서도 완전히 동일합니다.

## Step 1: 프로젝트 기초 코드 셋업 (Skeleton)
이전 튜토리얼에서 만든 순수 jOOQ 뼈대 프로젝트 구조(Java/Kotlin Spring Boot)를 복사하여 `code-java`, `code-kotlin` 으로 배치합니다.
- Database: `jooq_rbac` 라는 스키마/데이터베이스를 사용합니다.
- `flyway` 또는 수동 SQL로 `users`, `role`, `permission`, `user_role`, `role_permission` 5개 테이블을 생성합니다.
- `jooq-codegen`을 실행하여 Q 클래스들과 Record 클래스들을 생성합니다.

## Step 2: 도메인 데이터 구조 설계 (DTO 정의)
N:M 관계를 계층적으로 표현하기 위한 DTO를 작성합니다. (Java Record 클래스 활용 추천)

```java
// 권한 DTO
public record PermissionDto(Long id, String resource, String action) {}

// 역할 DTO (권한 목록 포함)
public record RoleDto(Long id, String name, List<PermissionDto> permissions) {}

// 사용자 통합 DTO (역할 목록 포함)
public record UserProfileDto(
    Long id, 
    String username, 
    String status,
    List<RoleDto> roles
) {}
```

## Step 3: [핵심 1] 단일 쿼리(MultiSet)로 사용자 권한 프로필 조회 (UserQueryRepository.java)
어플리케이션에서 무거운 부분인 "사용자에 매핑된 전체 역할과 권한 트리 다 가져오기"를 쿼리 한 번에 해결합니다.

```java
public UserProfileDto getUserProfileWithPermissions(String username) {
    // Aliases
    Users u = USERS;
    UserRole ur = USER_ROLE;
    Role r = ROLE;
    RolePermission rp = ROLE_PERMISSION;
    Permission p = PERMISSION;

    return dsl.select(
            u.ID,
            u.USERNAME,
            u.STATUS,
            multiset(
                select(
                    r.ID,
                    r.NAME,
                    multiset(
                        select(p.ID, p.RESOURCE, p.ACTION)
                        .from(rp)
                        .join(p).on(rp.PERMISSION_ID.eq(p.ID))
                        .where(rp.ROLE_ID.eq(r.ID))
                    ).as("permissions").convertFrom(r -> r.map(mapping(PermissionDto::new)))
                )
                .from(ur)
                .join(r).on(ur.ROLE_ID.eq(r.ID))
                .where(ur.USER_ID.eq(u.ID))
            ).as("roles").convertFrom(r -> r.map(mapping(RoleDto::new)))
        )
        .from(u)
        .where(u.USERNAME.eq(username))
        .fetchOneInto(UserProfileDto.class);
}
```

## Step 4: [핵심 2] 특정 리소스 접근 권한 유무 빠른 검증 (PermissionRepository.java)
API 호출이 올 때마다 인가(Authorization) 처리를 해야 합니다. 다건 객체를 매핑하지 말고 빠르고 가볍게 `EXISTS` 로 처리합니다.

```java
public boolean hasPermission(Long userId, String resource, String action) {
    return dsl.fetchExists(
        selectOne()
        .from(USER_ROLE)
        .join(ROLE_PERMISSION).on(USER_ROLE.ROLE_ID.eq(ROLE_PERMISSION.ROLE_ID))
        .join(PERMISSION).on(ROLE_PERMISSION.PERMISSION_ID.eq(PERMISSION.ID))
        .where(USER_ROLE.USER_ID.eq(userId))
          .and(PERMISSION.RESOURCE.eq(resource))
          .and(PERMISSION.ACTION.eq(action))
    );
}
```

## Step 5: 비즈니스 서비스 구현 및 통합 테스트
- `AuthService`를 만들어 위의 두 메서드가 정상 작동하는지 확인합니다.
- 데이터베이스 초기화(더미 데이터 생성: ADMIN 룰, USER 룰 등) 로직을 통합 테스트의 `@BeforeEach`에 작성합니다.
- 테스트 시나리오:
  1. 관리자 권한을 가진 유저는 `hasPermission(adminId, "ARTICLE", "DELETE")` 가 `true` 를 반환해야 함.
  2. 일반 유저는 `false` 를 반환해야 함.
  3. `UserProfileDto` 변환 시, JSON 직렬화 구조와 동일하게 1차 배열(역할) - 2차 배열(권한) 이 완벽히 들고 와지는지 Assert 검증.
