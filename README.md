# matgipv1 — 부산 맛집 웹 서비스

부산 IT교육센터 팀 프로젝트(5명, 2026.09.21~2026.10.14)로 만든 부산 맛집 웹 서비스입니다.
식당 목록·상세 조회, 검색, 페이징, 등록·수정·삭제(CRUD), 리뷰·찜, 지도 표시 기능을 제공합니다.

## 팀 역할 분담

- **Place(식당) 도메인 — 오병석**: 목록·상세 조회, 검색(카테고리 + 키워드, DB 등록분 기준), 페이징(12개씩), CRUD·이미지 업로드·지도 API 직접 구현
- 나머지 도메인(회원·리뷰·찜·관리자)은 팀원이 담당

## 기술 스택

- Java, Spring Boot (WebMVC, Data JPA, Thymeleaf, Validation, Security, OAuth2 Client)
- QueryDSL, ModelMapper, Thymeleaf Layout Dialect
- MariaDB, Kakao / Naver 소셜 로그인, 지도 API

## 실행 방법

1. `.env.example`을 복사해 `.env` 파일을 만들고 비밀값을 채웁니다.
   ```bash
   cp .env.example .env
   ```
   - `DB_PASSWORD`: MariaDB 비밀번호
   - `OPENAPI_SERVICE_KEY`: 부산 맛집 공공데이터 포털 서비스키
   - `KAKAO_CLIENT_ID`, `KAKAO_CLIENT_SECRET`: 카카오 OAuth2 앱 정보
   - `NAVER_CLIENT_ID`, `NAVER_CLIENT_SECRET`: 네이버 OAuth2 앱 정보
2. MariaDB에 `webdb` 데이터베이스를 만듭니다.
3. 실행합니다.
   ```bash
   ./gradlew bootRun
   ```
   - 접속: http://localhost:8081

> `.env` 파일은 비밀정보를 담고 있어 Git에 올리지 않습니다. (`.gitignore`에 등록됨)
> `application.properties`의 비밀값은 `${...}` 자리표시자로 환경 변수를 참조합니다.
