# 오작교(OJK) 프로젝트 구조/아키텍처 리뷰

## 1. 폴더 구조 설명

- `src/main/java/com/seoulorigin/OJK`
  - `OjkApplication.java`: 스프링부트 메인 엔트리포인트.
  - `domain/`
    - `auth/`: 로그인, 이메일 인증, JWT 토큰 발급 관련 기능.
      - `controller/`: 인증 API 엔드포인트.
      - `service/`: 인증/이메일 발송 비즈니스 로직.
      - `repository/`: 이메일 인증 상태 저장소(현재 Redis 기반).
      - `jwt/`: JWT 생성 로직.
      - `dto/`: 인증 요청/응답 DTO.
    - `member/`: 사용자(회원) 핵심 도메인.
      - `entity/`: Neo4j 노드 모델(`Member`).
      - `repository/`: 검색/최단경로/팔로워 조회 쿼리.
      - `service/`: 회원가입/검색/경로 탐색 로직.
      - `controller/`: 회원 관련 API.
      - `dto/`: 회원가입 요청/회원 응답 DTO.
    - `follow/`: 팔로우 관계 생성 및 팔로워 조회.
    - `major/`: 전공(Major) 노드 및 저장소.
    - `common/`: 공통 추상 엔티티(감사 필드), 보안 설정.
  - `global/config/`: Neo4j 감사 기능 등 글로벌 설정.
- `src/main/resources`
  - `application.properties`: Neo4j/메일/JWT/시크릿 import 설정.
  - `static/index.html`: 정적 페이지.
- `src/test/java`: 기본 스모크 테스트(`OjkApplicationTests`).
- 루트
  - `build.gradle`: Spring Boot + Neo4j + Security + Redis + Mail 의존성.
  - `docker-compose.yml`, `Dockerfile`: 인프라/실행 환경 정의.
  - `http/http-client.http`: API 수동 테스트 스크립트.

## 2. 주요 모듈 역할

- **Member 도메인(핵심 모델)**
  - `Member`는 `FOLLOWS`, `BELONGS_TO` 관계를 가진 그래프 중심 엔티티.
  - `MemberRepository`는 통합검색/팔로워 조회/최단 경로(`shortestPath`)를 직접 Cypher로 수행.
  - `MemberService`는 회원가입 시 이메일 인증 여부 체크 + 비밀번호 암호화 + 전공 연결까지 담당.

- **Follow 도메인(관계 관리)**
  - `FollowService.follow(from, to)`에서 자기 자신 팔로우 방지 후 `Member.follow()`로 관계 생성.
  - 팔로워 목록 조회는 `MemberRepository.findFollowersById` 쿼리 재사용.

- **Auth 도메인(인증/인가)**
  - `AuthService`는 이메일/비밀번호 검증 후 JWT 발급.
  - `EmailService`는 인증번호 생성 및 SMTP 메일 전송.
  - `VerificationStore`는 인증번호/인증상태 TTL 저장(현재 Redis).
  - `JwtTokenProvider`는 secret 기반 서명 토큰 생성(현재 생성 전용).

- **설정 계층**
  - `SecurityConfig`는 현재 Stateless 설정이지만 실제 인가를 막지 않고 `permitAll` 상태.
  - `Neo4jConfig`는 Neo4j 감사(auditing) 활성화.

## 3. 의존성 구조

- **외부 시스템 의존성**
  - Neo4j: 회원/전공 노드 및 팔로우 그래프 관계 저장.
  - Redis: 이메일 인증코드/인증상태 임시 저장.
  - SMTP(Gmail): 인증 이메일 발송.

- **내부 계층 의존성(대략적 흐름)**
  - `Controller -> Service -> Repository(Neo4j/Redis)`
  - `Service -> JwtTokenProvider/PasswordEncoder` 같은 인프라 컴포넌트 주입.

- **도메인 간 의존성**
  - `AuthController`가 인증 API뿐 아니라 `MemberService`를 직접 호출해 회원가입까지 처리.
  - `FollowService`가 별도 FollowRepository 없이 MemberRepository에 의존.
  - `MemberService`가 `MajorRepository` + `VerificationStore`를 함께 의존.

- **빌드 의존성 관점**
  - Spring Boot starters(webmvc/security/validation/mail/data-neo4j/data-redis)
  - JWT(`jjwt-*`) + Lombok + Apache commons-lang3.

### 3.1 의존 흐름 요약(레이어)

```text
API 요청
  -> Controller(Auth/Member/Follow)
    -> Service(AuthService/MemberService/FollowService)
      -> Repository(MemberRepository/MajorRepository/VerificationStore)
        -> 외부 인프라(Neo4j/Redis/SMTP)
```

- 인증 흐름은 `AuthController -> AuthService -> JwtTokenProvider`로 연결되고,
  회원가입 흐름은 `AuthController|MemberController -> MemberService -> (VerificationStore, MajorRepository, MemberRepository)` 경로를 탄다.
- 팔로우 흐름은 `FollowController -> FollowService -> MemberRepository`로 단순하며,
  현재는 Follow 전용 저장소 계층 없이 Member 저장소에 결합되어 있다.

## 4. 개선이 필요한 부분

1. **보안 설정 실효성 복구 필요(가장 우선)**
   - 현재 `anyRequest().permitAll()`이라 로그인/JWT를 발급해도 보호되는 API가 없음.
   - JWT 검증 필터, 인증 객체 주입, endpoint별 권한정책 복구가 필요.

   - **진행 계획(보안 설정 실효성 복구 로드맵)**
     1) **엔드포인트 권한 정책 확정**
        - 공개 API(`회원가입/로그인/이메일 인증`)와 인증 필요 API(`팔로우/팔로워 조회/경로 조회`)를 표로 먼저 확정.
        - `/api/auth/**`는 `permitAll`, 그 외 `/api/member/**` 일부와 팔로우 관련 API는 `authenticated`로 분리.
     2) **JWT 검증 파이프라인 추가**
        - `OncePerRequestFilter` 기반 JWT 인증 필터를 도입해 요청 헤더의 Bearer 토큰을 파싱/검증.
        - 토큰 유효 시 `SecurityContext`에 인증 객체를 저장하고, 실패 시 401 응답을 표준화.
     3) **SecurityConfig 정책 복구**
        - 현재 `anyRequest().permitAll()`을 제거하고 `requestMatchers` 기반 화이트리스트 정책으로 복구.
        - 세션은 계속 `STATELESS` 유지, CORS/CSRF 정책은 API 성격에 맞게 명시.
     4) **도메인 API 인증정보 연계**
        - `fromId`를 URL로 직접 받는 방식은 위변조 위험이 있어, 인증 주체(`principal`) 기준으로 팔로우 주체를 결정하도록 개선.
        - 서비스 레이어에서 "토큰 사용자 == 요청 사용자" 검증을 추가.
     5) **예외/응답 포맷 표준화**
        - 인증 실패(401), 권한 없음(403), 토큰 만료/위조를 `@RestControllerAdvice` 혹은 EntryPoint/AccessDeniedHandler로 통일.
        - 프론트가 처리 가능한 에러 코드(예: `AUTH_EXPIRED`, `AUTH_INVALID`)를 정의.
     6) **테스트 및 점진적 전환**
        - `spring-security-test`로 공개/보호 엔드포인트 접근 제어 테스트를 작성.
        - 1차로 읽기 API부터 보호 후, 2차로 쓰기 API까지 확장하는 단계적 릴리즈 전략 적용.

2. **인증/회원가입 책임 분리 재정리**
   - `/api/auth/signup`와 `/api/member/signup`가 중복 존재.
   - AuthController가 MemberService까지 직접 다뤄 경계가 모호하므로 하나의 진입점으로 단순화 필요.

3. **예외 처리 표준화**
   - `IllegalArgumentException` 위주로 처리되어 API 응답 형식/상태코드가 일관적이지 않음.
   - `@RestControllerAdvice` + 에러 코드 체계 도입 권장.

4. **팔로우 도메인 무결성 강화**
   - 중복 팔로우 방지, 언팔로우, 팔로우 요청/승인 모델 부재(README TODO와 동일).
   - 관계 존재 여부 검사 쿼리 및 트랜잭션 정책 보강 필요.

5. **경로 탐색 API 응답 개선**
   - 현재 `/api/member/path`는 `Member` 엔티티를 직접 반환해 민감필드(예: password) 노출 위험.
   - 전용 경로 DTO로 변환하고 최소 필드만 응답해야 함.

6. **비밀번호/토큰 보안 강도 강화**
   - 비밀번호 정책(길이/복잡도) 검증 부족.
   - JWT는 생성만 있고 파싱/검증/만료 처리 파이프라인 미구현.

7. **테스트 확충 필요**
   - 현재 테스트가 앱 로딩 수준 1개뿐.
   - 서비스 단위 테스트(회원가입/팔로우/경로), 저장소 통합 테스트(Cypher), 보안 테스트를 추가해야 함.

8. **구성/설정 관리 개선**
   - `application.properties`의 로컬 고정 URI 및 메일 설정은 프로파일 분리(`local/dev/prod`) 권장.
   - 시크릿 로딩 실패 시 대응 전략(부팅 실패 메시지/문서화) 보강 필요.
