# Place 기능 발표자료

> 팀 전체 발표 목차에 맞춘 Place 담당 발표자료
>
> 기준 화면: `src/main/resources/templates/place/read.html`

## 전체 발표 목차

1. 프로젝트 개요: 배경, 목표, 기능, 팀, 일정(2주)
2. 기술과 설계: 기술 스택, 아키텍처, 유즈케이스, ERD
3. 주요 화면: 사용자 화면, 관리자 화면
4. 핵심 구현: 공공데이터 동기화, 마일리지, 보안
5. 트러블 슈팅: 실제 문제와 해결 과정
6. 품질과 회고: 테스트, 배운 점, 다음 단계

## Place 담당 발표 범위

- 발표 시간: 약 5~7분
- 핵심 메시지: 사용자가 맛집을 찾고, 위치와 후기를 확인한 뒤 방문을 결정하도록 돕는 기능
- 연결 기능: 회원·리뷰·찜·관리자·공공데이터 동기화
- 시연 순서: 맛집 목록 → 상세 조회 → 지도 확인 → 찜하기 → 리뷰 확인 → 관리자 기능

> 팀원 정보와 실제 일정은 저장소에서 확인할 수 없으므로, 아래 프로젝트 개요의 `[입력 필요]` 항목에 팀에서 정한 내용을 넣어주세요. 마일리지 역시 현재 소스에서 관련 Entity·Service·Controller가 확인되지 않아, 구현된 기능으로 발표하지 않고 “미구현/다음 단계”로 구분하는 것이 정확합니다.

---

## 1. 프로젝트 개요

### 배경

부산 맛집 정보는 지역, 메뉴, 영업시간, 위치, 후기처럼 여러 정보가 흩어져 있어 사용자가 방문 전에 비교하기 어렵습니다. 이 프로젝트는 부산 맛집 정보를 한 곳에 모아 검색·상세 조회·지도 확인·후기 확인까지 이어지는 흐름을 제공합니다.

### 목표

- 지역·카테고리·키워드로 부산 맛집을 빠르게 검색
- 주소와 카카오 지도로 실제 위치 확인
- 별점과 방문자 리뷰를 이용한 방문 판단 지원
- 찜 기능으로 관심 맛집 저장
- 관리자가 맛집 데이터와 리뷰를 관리

### 주요 기능

- 사용자: 맛집 검색, 상세 조회, 지도, 리뷰 작성, 찜하기, 랜덤 맛집 추천
- 관리자: 맛집 등록·수정·삭제, 공공데이터 동기화, 회원 관리, 리뷰 숨김·삭제
- 데이터: 부산 공공데이터 API 기반 맛집 신규 데이터 수집

### 팀과 일정

- 팀 구성: `[팀원 이름 및 담당 기능 입력]`
- 개발 기간: 2주
- 1주차: 요구사항 정리, DB·화면 설계, 기본 CRUD와 로그인 구현
- 2주차: 지도·리뷰·찜·공공데이터 동기화, 권한 처리, 통합 점검 및 발표 준비

### 프로젝트를 시작한 이유

맛집을 찾을 때 사용자는 보통 다음 정보를 동시에 확인합니다.

- 내가 가려는 지역에 있는지
- 어떤 메뉴를 파는지
- 지금 영업 중인지
- 실제 위치가 어디인지
- 다른 사람이 방문해 본 곳인지

기존에는 이 정보가 지도, 블로그, 리뷰 서비스 등에 흩어져 있습니다. 이 프로젝트는 부산 맛집 데이터를 하나의 서비스 안에 모으고, 검색에서 방문 결정까지의 단계를 줄이는 것을 목표로 했습니다.

### Place 담당자의 핵심 기여

- 맛집 목록·상세 조회 화면 구현
- 지역·카테고리·키워드 검색 및 페이지네이션
- 카카오 지도 기반 위치 표시
- 리뷰와 평균 별점 표시
- 찜하기 AJAX 처리
- 관리자 맛집 등록·수정·삭제
- 공공데이터 신규 맛집 동기화 흐름 연결

### 2주 일정 상세 예시

| 기간 | 작업 | Place 결과물 |
|---|---|---|
| 1~2일차 | 요구사항·화면 흐름 정리 | 목록·상세·관리자 화면 구조 |
| 3~4일차 | Entity·Repository 설계 | `Place`, `Review`, `Bookmark` 기반 조회 구조 |
| 5~7일차 | 목록·상세 CRUD 구현 | 검색, 페이지 이동, 상세 정보 출력 |
| 8~9일차 | 외부 API·지도 연동 | 공공데이터 저장, 주소 지오코딩, 지도 마커 |
| 10~11일차 | 사용자 기능 구현 | 리뷰, 찜, 이미지 업로드 |
| 12~13일차 | 권한·예외·화면 보완 | 관리자 권한, 로그인 안내, 오류 메시지 |
| 14일차 | 시나리오 점검·발표 준비 | 사용자·관리자 시연 시나리오 |

---

## 발표 슬라이드에 넣을 시각 자료

발표 화면이 텍스트만 보이지 않도록 다음 캡처를 준비하면 좋습니다.

1. 맛집 목록: 지역 필터와 카드 목록이 보이는 화면
2. 맛집 상세: 대표 이미지, 평균 별점, 주소, 찜 버튼
3. 상세 화면 하단: 카카오 지도와 최신 리뷰
4. 리뷰 전체보기 모달: 별점·혼밥 레벨·사진
5. 관리자 등록 화면: 기본 정보와 이미지 미리보기
6. 관리자 화면: 공공데이터 동기화 버튼과 결과 메시지
7. ERD: `MEMBER`, `BUSAN_RESTAURANTS`, `REVIEW`, `BOOKMARK`, `MEMBER_LOG_DATA`

각 캡처 아래에는 “사용자에게 어떤 결정을 돕는 화면인지”를 한 문장으로 적습니다.

---

---

## 슬라이드 1. Place 기능 소개

### 화면에 넣을 내용

**Place: 부산 맛집 정보를 탐색하고 관리하는 핵심 기능**

- 지역·카테고리·검색어로 맛집 탐색
- 맛집 상세 정보와 위치 확인
- 평균 별점 및 방문자 리뷰 확인
- 로그인 사용자의 찜하기
- 관리자의 맛집 등록·수정·삭제

### 발표 원고

“Place 기능은 서비스의 중심 기능입니다. 사용자는 부산 지역의 맛집을 검색하고 상세 정보를 확인할 수 있습니다. 상세 화면에서는 메뉴와 영업시간뿐 아니라 카카오 지도, 평균 별점, 방문자 리뷰까지 한 화면에서 확인할 수 있습니다. 관리자는 맛집 정보를 등록하고 수정하며, 사용자는 마음에 드는 맛집을 찜하거나 직접 리뷰를 남길 수 있습니다.”

---

## 슬라이드 2. 사용자 이용 흐름

### 화면에 넣을 내용

```text
맛집 목록
   ↓ 지역·카테고리·키워드 검색
맛집 상세 조회
   ├─ 기본 정보 및 이미지 확인
   ├─ 지도에서 위치 확인
   ├─ 평균 별점과 최근 리뷰 확인
   ├─ 찜하기
   └─ 리뷰 작성
```

### 발표 원고

“사용자는 목록 화면에서 조건을 좁힌 뒤 상세 화면으로 이동합니다. 상세 화면은 방문 전에 필요한 정보를 모아 보여주는 역할을 합니다. 위치를 지도에서 확인하고, 다른 사용자의 평가를 읽은 다음, 찜하거나 리뷰를 남기는 흐름으로 이어집니다.”

---

## 슬라이드 3. 맛집 목록 및 검색

### 화면에 넣을 내용

- 지역구 필터: 해운대구, 부산진구, 수영구 등
- 카테고리 필터
- 맛집명·영문명·대표 메뉴·주소 기반 키워드 검색
- 페이지당 12건 조회 및 페이지 이동
- 목록에서도 평균 별점·리뷰 수·찜 수를 표시

### 구현 포인트

- `PlaceController.list()`가 목록 조회를 담당
- `PlaceServiceImpl.getList()`에서 페이지 정보를 적용
- `PlaceRepository.searchList()`의 JPQL 조건으로 지역·카테고리·키워드 검색
- 정렬 기준은 `pno` 내림차순

### 발표 원고

“목록 검색은 지역, 카테고리, 키워드를 조합할 수 있습니다. 키워드는 상호명뿐 아니라 대표 메뉴와 주소까지 검색하기 때문에 사용자가 알고 있는 정보만으로도 맛집을 찾을 수 있습니다. 결과는 페이지 단위로 제공하고, 각 목록 카드에는 상세 화면과 같은 별점·리뷰·찜 정보를 보여줍니다.”

---

## 슬라이드 4. 상세 화면의 정보 구성

### 화면에 넣을 내용

상세 페이지에는 다음 정보를 제공합니다.

| 영역 | 제공 정보 |
|---|---|
| 식당 식별 | 식당 번호, 지역구, 한글·영문 상호명 |
| 대표 정보 | 카테고리, 대표 메뉴, 이미지 2장 |
| 방문 정보 | 평균 별점, 리뷰 수, 주소, 전화번호 |
| 영업 정보 | 영업시간, 휴무일, 브레이크 타임 |
| 부가 정보 | 한국어·영어 소개글, 지도, 찜 수 |

### 구현 포인트

- Thymeleaf의 `dto` 객체를 사용해 서버 데이터를 화면에 바인딩
- 값이 없는 경우 `정보 없음`, `연중무휴`, `없음` 등의 기본 문구 표시
- 이미지가 없거나 로드에 실패하면 화면이 깨지지 않도록 처리
- 관리자에게만 수정·삭제 버튼 표시

### 발표 원고

“상세 화면은 맛집 방문 전에 확인해야 할 정보를 한 곳에 모았습니다. 대표 메뉴와 가격 관련 정보, 영업시간과 휴무일, 전화번호와 주소를 함께 제공합니다. 데이터가 없는 경우에도 기본 문구를 보여주기 때문에 화면 구성이 무너지지 않습니다. 관리자 계정으로 접속했을 때만 수정과 삭제 기능이 표시됩니다.”

---

## 슬라이드 5. 카카오 지도와 주소 검색

### 화면에 넣을 내용

```text
DB 주소
   ↓ 원본 주소 검색
성공 ─────→ 좌표 변환 → 지도 중심 이동 + 마커 표시
실패
   ↓ 괄호·층수 정보를 정리한 주소로 재검색
실패
   ↓ 부산 지역 + 상호명 키워드 검색
```

### 구현 포인트

- 카카오 지도 SDK의 `Geocoder`로 주소를 좌표로 변환
- 원본 주소 검색이 실패하면 괄호·층수 정보를 제거해 재검색
- 주소 검색도 실패하면 지역구와 상호명으로 키워드 검색
- 검색 성공 시 마커와 식당명을 표시하고 지도 중심을 이동

### 발표 원고

“주소 데이터는 입력 형식에 따라 검색이 실패할 수 있기 때문에 단계적으로 보정합니다. 먼저 원본 주소를 검색하고, 실패하면 괄호나 층수처럼 좌표 검색에 방해가 될 수 있는 정보를 제거합니다. 그래도 실패하면 지역구와 상호명으로 키워드 검색을 시도합니다. 이 과정을 통해 등록된 주소가 조금 달라도 위치를 표시할 가능성을 높였습니다.”

---

## 슬라이드 6. 리뷰 기능

### 화면에 넣을 내용

- 상세 화면에는 최신 리뷰 3개를 우선 표시
- “리뷰 전체보기” 모달에서 전체 리뷰 확인
- 로그인 사용자만 리뷰 작성 가능
- 별점 1~5점 선택
- 혼밥 난이도 LV1~LV5 선택
- 리뷰 내용 및 방문 사진 등록
- 작성자 본인 또는 관리자만 수정·삭제 가능

### 데이터 처리 흐름

```text
PlaceController.read()
   ├─ 전체 리뷰 조회
   ├─ 최신 리뷰 3개 조회
   ├─ 평균 별점 계산
   └─ 화면에 dto, reviews, topReviews 전달
```

### 발표 원고

“리뷰는 사용자의 방문 판단을 돕는 핵심 데이터입니다. 화면이 길어지지 않도록 최신 리뷰 3개를 먼저 보여주고, 전체 리뷰는 모달에서 확인할 수 있게 구성했습니다. 리뷰에는 별점뿐 아니라 혼밥 난이도와 사진을 함께 남길 수 있습니다. 리뷰 수정과 삭제는 작성자 본인 또는 관리자만 가능하도록 권한을 분리했습니다.”

---

## 슬라이드 7. 찜하기와 비동기 처리

### 화면에 넣을 내용

```text
찜하기 버튼 클릭
      ↓ fetch POST
/place/bookmark/toggle
      ↓
BookmarkService 토글 처리
      ↓
찜 여부·찜 수 JSON 응답
      ↓
버튼 색상·문구·카운트 즉시 변경
```

### 구현 포인트

- `fetch()`로 서버에 POST 요청
- 로그인하지 않은 경우 HTTP 401 응답 후 로그인 페이지로 이동
- 처리 후 페이지 전체를 새로고침하지 않고 버튼 상태를 즉시 갱신
- `❤️ 찜 완료`와 `🤍 찜하기` 상태를 시각적으로 구분

### 발표 원고

“찜하기는 페이지를 다시 로드하지 않고 동작하도록 AJAX 방식으로 구현했습니다. 버튼을 누르면 서버가 해당 사용자의 찜 상태를 토글하고, 변경된 찜 여부와 전체 찜 수를 JSON으로 반환합니다. 화면은 응답을 받아 버튼 색상, 문구, 카운트를 즉시 변경합니다. 로그인하지 않은 사용자는 401 응답을 받고 로그인 화면으로 안내됩니다.”

---

## 슬라이드 8. 서버 구조와 권한 처리

### 화면에 넣을 내용

```text
Browser / Thymeleaf
        ↓
PlaceController
        ↓
PlaceService / ReviewService / BookmarkService
        ↓
PlaceRepository / ReviewRepository / BookmarkRepository
        ↓
Database
```

### 주요 엔드포인트

| 기능 | 요청 경로 | 권한 |
|---|---|---|
| 목록 조회 | `GET /place/list` | 전체 공개 |
| 상세 조회 | `GET /place/read?pno=...` | 전체 공개 |
| 지도용 전체 목록 | `GET /place/api/places` | 전체 공개 |
| 리뷰 등록 | `POST /place/review/add` | 로그인 필요 |
| 찜 토글 | `POST /place/bookmark/toggle` | 로그인 필요 |
| 맛집 등록 | `POST /place/register` | 관리자 |
| 맛집 수정 | `POST /place/modify` | 관리자 |
| 맛집 삭제 | `POST /place/remove` | 관리자 |

### 발표 원고

“요청은 Controller에서 받고, 실제 데이터 처리는 Service를 거쳐 Repository에서 수행합니다. Place 자체의 CRUD와 리뷰, 찜 기능을 각각의 서비스로 나누어 책임을 분리했습니다. 조회 기능은 공개하고, 리뷰와 찜은 로그인 여부를 확인하며, 맛집 데이터 변경은 관리자 권한을 확인한 뒤 처리합니다.”

---

## 슬라이드 9. 현재 적용된 ERD

### Place 중심 ERD

```mermaid
erDiagram
    MEMBER ||--o{ REVIEW : writes
    MEMBER ||--o{ BOOKMARK : saves
    BUSAN_RESTAURANTS ||--o{ REVIEW : has
    BUSAN_RESTAURANTS ||--o{ BOOKMARK : has
    MEMBER ..o{ MEMBER_LOG_DATA : logs

    MEMBER {
        BIGINT member_no PK
        VARCHAR email UK
        VARCHAR password
        VARCHAR nickname
        VARCHAR profile_image
        VARCHAR role
        VARCHAR provider
        VARCHAR provider_id
    }

    BUSAN_RESTAURANTS {
        BIGINT id PK
        VARCHAR gu_name
        VARCHAR restaurant_name_ko
        VARCHAR restaurant_name_en
        VARCHAR category
        VARCHAR address
        VARCHAR phone
        VARCHAR main_menu
        VARCHAR price_range
        VARCHAR operating_hours
        VARCHAR break_time
        VARCHAR off_days
        VARCHAR parking
        VARCHAR image_url1
        VARCHAR image_url2
        VARCHAR summary_ko
        VARCHAR summary_en
        INT page_num
        VARCHAR source_type
        VARCHAR api_id
        DATETIME regdate
        DATETIME moddate
    }

    REVIEW {
        BIGINT id PK
        BIGINT restaurant_id FK
        BIGINT member_id FK
        INT rating
        VARCHAR solo_level
        TEXT content
        BOOLEAN hidden
        VARCHAR original_image_name
        VARCHAR store_image_name
        VARCHAR thumbnail_name
        DATETIME regdate
        DATETIME moddate
    }

    BOOKMARK {
        BIGINT id PK
        BIGINT member_id FK
        BIGINT restaurant_id FK
        DATETIME regdate
        DATETIME moddate
    }

    MEMBER_LOG_DATA {
        BIGINT log_id PK
        BIGINT member_id
        VARCHAR email
        VARCHAR nickname
        VARCHAR role
        VARCHAR action_type
        VARCHAR description
        VARCHAR request_url
        VARCHAR http_method
        VARCHAR ip_address
        VARCHAR user_agent
        VARCHAR device_type
        VARCHAR referer
        BIGINT target_id
        VARCHAR target_name
        VARCHAR search_keyword
        VARCHAR extra_details
        INT status_code
        BIGINT execution_time_ms
        DATETIME regdate
        DATETIME moddate
    }
```

### 관계와 제약조건

### 발표용 ERD 이미지

예시 이미지처럼 테이블 카드, 컬럼, PK·FK, 관계선까지 한 장에 배치한 SVG를 만들었습니다.

[place-erd.svg](C:\Users\it\IdeaProjects\matgipv1\docs\place-erd.svg)

PPT에서는 이 이미지를 ERD 슬라이드의 본문 이미지로 사용하고, 오른쪽에 다음 세 문장만 덧붙이면 됩니다.

- `busan_restaurants`가 Place 기능의 중심 테이블
- `review`와 `bookmark`가 `restaurant_id`로 맛집을 참조
- `member_log_data`는 회원·맛집 활동을 기록하는 독립 로그 테이블

| 관계 | 연결 컬럼 | 현재 적용 방식 |
|---|---|---|
| 회원-리뷰 | `review.member_id → member.member_no` | 엔티티 연관관계 없이 ID 값으로 연결 |
| 맛집-리뷰 | `review.restaurant_id → busan_restaurants.id` | `ReviewRepository` 조회 조건으로 연결 |
| 회원-찜 | `bookmark.member_id → member.member_no` | 회원별 찜 목록 조회와 회원 탈퇴 시 삭제 |
| 맛집-찜 | `bookmark.restaurant_id → busan_restaurants.id` | 맛집별 찜 수 조회와 찜 토글에 사용 |

### 핵심 설명

- `busan_restaurants`가 Place 기능의 중심 테이블입니다.
- 프로젝트 전체 Entity 기준으로는 `member_log_data`까지 포함해 총 5개 테이블입니다.
- `review`는 한 회원이 같은 맛집에 중복 리뷰를 남기지 못하도록 `(restaurant_id, member_id)` 유니크 제약을 가집니다.
- `bookmark`는 한 회원이 같은 맛집을 중복 찜하지 못하도록 `(member_id, restaurant_id)` 유니크 제약을 가집니다.
- 두 테이블 모두 `BaseEntity`를 상속해 `regdate`, `moddate`를 사용합니다.
- `member_log_data`는 로그인·검색·맛집 조회·리뷰·동기화 등의 활동을 기록하는 로그 테이블입니다. `member_id`를 저장하지만 JPA FK 연관관계는 없습니다.

### 발표 원고

“프로젝트의 Entity 기준 테이블은 회원, 맛집, 리뷰, 찜, 활동 로그까지 총 5개입니다. Place 기능의 핵심 관계는 회원과 맛집이 리뷰와 찜을 통해 연결되는 구조입니다. 활동 로그는 회원 번호와 대상 번호를 기록하지만 별도의 JPA FK 연관관계는 없습니다. 또한 현재 프로젝트는 `@ManyToOne`이나 `@OneToMany`를 사용하지 않고, `restaurant_id`와 `member_id`를 직접 저장해 Service와 Repository에서 연결합니다.”

### 구현상 확인할 점

`BookmarkRepository`에는 맛집 삭제 시 찜 데이터를 지우는 `deleteByRestaurantId()`가 정의되어 있습니다. 하지만 현재 `PlaceServiceImpl.remove()`는 리뷰 삭제 후 맛집을 삭제하는 흐름으로 구현되어 있어, Place 삭제 시 찜 데이터 정리 호출이 포함되어 있지 않습니다. 따라서 현재 ERD 발표에서는 “논리적 관계”로 설명하고, 실제 운영 전에는 맛집 삭제 시 Bookmark도 함께 삭제하도록 보완하는 것이 안전합니다.

---

## 슬라이드 10. 관리자용 맛집 관리

### 화면에 넣을 내용

```text
관리자
  ├─ C · 등록
  │    place/register.html
  │    POST /place/register
  │    → PlaceService.register()
  │    → PlaceRepository.save()
  │
  ├─ U · 수정
  │    place/modify.html
  │    POST /place/modify
  │    → PlaceService.modify()
  │    → Place.change() 후 save()
  │
  └─ D · 삭제
       place/read.html 또는 place/modify.html
       POST /place/remove
       → ReviewRepository.deleteByRestaurantId()
       → PlaceRepository.deleteById()
```

| 구분 | 화면 | 엔드포인트 | 처리 내용 |
|---|---|---|---|
| C · Create | `place/register.html` | `POST /place/register` | 맛집 정보와 이미지 저장 후 Place 등록 |
| U · Update | `place/modify.html` | `POST /place/modify` | 기존 Place 조회 후 `change()`로 수정 |
| D · Delete | `place/read.html`, `place/modify.html` | `POST /place/remove` | 리뷰 삭제 후 Place 삭제 |

### 등록·수정·삭제 상세

- 한글·영문 상호명, 지역구, 카테고리 입력
- 주소, 전화번호, 대표 메뉴, 가격대, 주차 정보 입력
- 영업시간, 휴무일, 브레이크 타임 입력
- 대표 이미지 2장: 파일 업로드 또는 URL 입력
- 파일 선택 시 브라우저에서 이미지 미리보기
- 서버에서 `ROLE_ADMIN`을 확인한 경우에만 CUD 처리
- `list.html`은 조회 화면이며 관리자 등록 화면으로 이동하는 진입점을 제공합니다.
- 상세 화면에는 관리자에게만 수정·삭제 버튼이 표시됩니다.

### 발표 원고

“Place 기능은 조회만 있는 것이 아니라 관리자용 CUD까지 포함합니다. 등록에서는 맛집 정보와 이미지를 입력해 `PlaceRepository.save()`를 호출하고, 수정에서는 기존 Place를 조회한 뒤 `change()`로 값을 변경합니다. 삭제 시에는 리뷰를 먼저 정리한 뒤 맛집을 삭제합니다. 세 작업 모두 서버에서 관리자 권한을 확인하며, 일반 사용자는 목록과 상세 정보만 조회할 수 있습니다.”

---

## 슬라이드 11. 시연 순서와 마무리

### 시연 체크리스트

1. `/place/list`에서 지역구 또는 키워드 검색
2. 맛집 카드의 상세보기로 이동
3. 상세 정보와 이미지 확인
4. 지도 마커와 위치 확인
5. 리뷰 전체보기 모달 열기
6. 로그인 후 찜하기 클릭 → 찜 수와 버튼 상태 확인
7. 별점·혼밥 레벨·내용·사진으로 리뷰 등록
8. 관리자 계정으로 수정 버튼과 등록 화면 확인

### 마무리 원고

“Place 기능은 맛집 데이터를 보여주는 화면을 넘어, 검색부터 위치 확인, 사용자 평가, 개인 저장, 관리자 데이터 관리까지 연결합니다. 특히 상세 화면을 중심으로 지도와 리뷰, 찜 기능을 통합해 사용자가 맛집을 선택하는 데 필요한 정보를 한 흐름 안에서 제공한 점이 핵심입니다.”

---

---

# 6개 목차 기준 발표용 요약

## 2. 기술과 설계

### 기술 스택

- Backend: Java 21, Spring Boot 4.1.1, Spring MVC
- Data: Spring Data JPA, QueryDSL, MariaDB
- View: Thymeleaf, Bootstrap 5, JavaScript
- 인증: Spring Security, Kakao·Naver OAuth2, 세션 인증
- 외부 연동: 부산 공공데이터 API, Kakao Maps API
- Build: Gradle

### 아키텍처

```text
Thymeleaf / JavaScript 화면
          ↓
Controller: 요청·권한·화면 이동
          ↓
Service: Place·Review·Bookmark·OpenApi 업무 처리
          ↓
Repository: JPA 기반 DB 조회·저장
          ↓
MariaDB
```

### 유즈케이스

| 사용자 | 유즈케이스 |
|---|---|
| 비로그인 사용자 | 맛집 목록·검색·상세·지도·리뷰 조회 |
| 로그인 사용자 | 찜하기, 리뷰 등록·수정·삭제 |
| 관리자 | 맛집 등록·수정·삭제, 공공데이터 동기화, 회원·리뷰 관리 |

### ERD 발표 포인트

`MEMBER`, `BUSAN_RESTAURANTS`, `REVIEW`, `BOOKMARK`를 중심으로 설명합니다. 실제 코드는 JPA 객체 연관관계 대신 `member_id`, `restaurant_id`를 직접 저장하는 방식입니다. 자세한 Mermaid ERD는 위의 “현재 적용된 ERD” 슬라이드를 사용하면 됩니다.

---

## 3. 주요 화면

### 사용자 화면

1. 맛집 목록: 지역·카테고리·검색어 필터, 페이지네이션, 평균 별점·리뷰 수·찜 수
2. 맛집 상세: 이미지, 메뉴, 주소, 전화번호, 영업시간, 지도
3. 리뷰: 최신 3개 미리보기, 전체보기 모달, 별점·혼밥 레벨·사진 등록
4. 찜: 버튼 클릭 후 페이지 새로고침 없이 상태와 개수 갱신
5. 랜덤 추천: 지역·카테고리 조건으로 오늘의 맛집 추천

### 관리자 화면

- 맛집 등록: 기본 정보와 이미지 파일·URL 입력
- 맛집 수정·삭제: 관리자 권한 확인 후 처리
- 공공데이터 동기화: 100건 단위 또는 전체 페이지 동기화
- 리뷰 관리: 리뷰 숨김·복원·강제 삭제
- 회원 관리: 회원 조회 및 정지·정지 해제

---

## 4. 핵심 구현

### 공공데이터 동기화

`OpenApiService`가 부산 공공데이터 API의 `getFoodKr`를 호출하고, 100건 단위로 데이터를 처리합니다.

```text
관리자 동기화 요청
      ↓
공공데이터 API 호출
      ↓
응답 형식·API 상태 검증
      ↓
api_id 또는 상호명+주소로 중복 확인
      ↓
신규 데이터만 busan_restaurants에 저장
```

중복 데이터는 건너뛰고, API 오류·빈 응답·인증 오류를 상태값으로 구분해 관리자 화면에 전달합니다.

### 마일리지

현재 저장소에서는 `Mileage` 또는 `Point` Entity, Repository, Service, Controller가 확인되지 않았습니다. 따라서 현재 구현 기준 발표에서는 마일리지를 완료 기능으로 설명하면 안 됩니다.

발표 문구는 다음처럼 정리하는 것이 정확합니다.

> “마일리지 기능은 아이디어 또는 다음 단계로 검토 중이며, 현재 버전에서는 리뷰·찜·랜덤 추천 기능을 우선 구현했습니다.”

### 보안

- Spring Security로 로그인과 권한을 관리
- Kakao·Naver OAuth2 로그인 지원
- 관리자 요청마다 `ROLE_ADMIN` 확인
- 리뷰 수정·삭제는 작성자 본인 또는 관리자만 허용
- 비로그인 사용자가 리뷰·찜 기능을 호출하면 로그인 안내
- 세션은 5분 무활동 시 만료
- Thymeleaf 화면에 CSRF 토큰을 전달

---

## 5. 트러블 슈팅

### 문제 1. 주소 형식에 따라 지도 검색이 실패

- 원인: 괄호 정보나 층수 정보가 포함된 주소는 지오코딩 결과가 나오지 않는 경우가 있음
- 해결: 원본 주소 → 괄호·층수 제거 주소 → 지역구+상호명 키워드 순서로 재검색
- 결과: 다양한 주소 입력 형식에 대응하고 검색 실패 가능성을 낮춤

### 문제 2. 공공데이터 중복 저장 가능성

- 원인: API 재동기화 때 동일 맛집이 다시 들어올 수 있음
- 해결: `api_id`를 먼저 확인하고, 값이 없으면 상호명과 주소 조합으로 중복 확인
- 결과: 기존 데이터는 건너뛰고 신규 데이터만 저장

### 문제 3. 리뷰와 찜의 권한 처리

- 원인: 화면에서 버튼을 숨기는 것만으로는 직접 요청을 막을 수 없음
- 해결: Controller에서 세션 회원 번호와 역할을 다시 확인
- 결과: 작성자 본인·관리자만 리뷰 변경 가능, 비로그인 사용자는 로그인으로 이동

### 문제 4. 맛집 삭제 시 연관 데이터 정리

- 현재 상태: `PlaceServiceImpl.remove()`에서 리뷰 삭제 후 맛집을 삭제
- 확인 사항: `BookmarkRepository`에는 찜 일괄 삭제 메서드가 있으나 삭제 서비스에서 호출되지 않음
- 다음 조치: 맛집 삭제 시 리뷰와 찜을 함께 삭제하도록 보완

---

## 6. 품질과 회고

### 테스트 현황

- Spring Boot 컨텍스트 테스트: `Matgipv1ApplicationTests`
- Place Repository 테스트: `PlaceRepositoryTests`
- 빌드·컴파일 과정에서 QueryDSL Q 클래스 생성
- 발표 전 확인 항목: 검색 조건, 관리자 권한, 리뷰 권한, 찜 중복 방지, API 중복 동기화

### 배운 점

- 화면 기능은 Controller 하나만으로 완성되지 않고 Service·Repository·권한·외부 API가 함께 맞물려야 함
- 외부 주소와 공공데이터는 항상 실패하거나 형식이 달라질 수 있어 예외 흐름이 필요함
- 논리적 FK를 ID로 관리할 때는 삭제 순서와 고아 데이터 정리를 직접 설계해야 함
- 사용자 화면과 관리자 화면의 권한을 서버에서 각각 검증해야 함

### 다음 단계

- 맛집 삭제 시 Bookmark 정리 로직 추가
- `member_id`, `restaurant_id`에 대한 실제 DB FK 또는 명시적인 무결성 정책 검토
- Place 목록에서 리뷰를 맛집마다 조회하는 구조 개선
- 마일리지 기능 도입 시 `mileage_history` 테이블과 적립·차감 정책 설계
- 주소 좌표를 DB에 저장해 상세 화면마다 재검색하지 않도록 개선
- API·Controller·Service 단위 테스트 확대

### 품질을 확인할 사용자 시나리오

| 시나리오 | 확인할 결과 |
|---|---|
| 지역구를 선택하고 검색 | 선택한 지역의 맛집만 조회 |
| 키워드로 메뉴 검색 | 상호명·대표 메뉴·주소에 포함된 결과 조회 |
| 맛집 상세 페이지 진입 | 이미지·운영 정보·지도·리뷰 표시 |
| 비로그인 상태에서 찜 클릭 | 401 처리 후 로그인 안내 |
| 로그인 후 찜 클릭 | 찜 여부와 찜 수 즉시 변경 |
| 리뷰 등록 | 상세 페이지로 돌아오고 리뷰 반영 |
| 다른 사용자의 리뷰 수정 시도 | 권한 없음 처리 |
| 관리자 외 맛집 등록 요청 | 목록 페이지로 돌려보내고 오류 표시 |
| 공공데이터 재동기화 | 중복 데이터는 건너뛰고 신규 데이터만 저장 |
| 주소 검색 실패 | 주소 정제·상호명 검색으로 재시도 |

### 현재 품질의 한계

- Repository와 Spring Boot 컨텍스트 테스트는 있으나 모든 Controller 권한 시나리오가 자동화된 상태는 아님
- 목록에서 각 맛집의 리뷰를 별도로 조회하므로 데이터가 많아지면 조회 횟수가 늘어날 수 있음
- 지도 좌표를 별도로 저장하지 않아 상세 페이지 진입 때 주소 검색을 수행함
- 실제 DB 전체 스키마는 애플리케이션 Entity 기준과 기존 DB 상태가 다를 수 있으므로 배포 전 스키마 확인이 필요함

### 회고 발표 원고

“이번 구현을 통해 화면을 만드는 것과 실제 서비스 흐름을 완성하는 것은 다르다는 점을 배웠습니다. 목록 하나를 보여주기 위해 검색 조건, 페이지 계산, 리뷰와 찜 집계, 로그인 상태까지 함께 고려해야 했습니다. 또한 외부 API와 지도는 입력 데이터가 항상 일정하다는 보장이 없기 때문에 성공 흐름뿐 아니라 재검색과 오류 처리까지 설계해야 했습니다.”

---

## 페이지별 발표 확장 원고

### 페이지 1. 프로젝트 개요

**화면 구성**

- 제목: “부산 맛집 탐방 서비스”
- 왼쪽: 문제 상황 3개 — 정보 분산, 위치 확인 불편, 후기 비교 어려움
- 오른쪽: 해결 흐름 — 검색 → 상세 확인 → 지도·리뷰 → 찜·방문
- 하단: 개발 기간 2주, 팀원 및 담당 기능

**발표 멘트**

“저희는 부산 맛집을 찾는 사용자가 여러 서비스로 이동하지 않고, 한 화면에서 방문 판단을 할 수 있도록 서비스를 구성했습니다. Place 기능은 이 흐름의 중심으로, 맛집 데이터 조회와 지도, 리뷰, 찜을 연결합니다.”

### 페이지 2. 기술과 설계

**화면 구성**

- 기술 스택 아이콘 또는 로고
- 브라우저 → Controller → Service → Repository → MariaDB 구조
- 아래에 외부 API 2개 배치: 부산 공공데이터 API, Kakao Maps API

**설명 포인트**

- Controller: 요청 파라미터와 세션을 확인하고 화면 또는 JSON 응답 반환
- Service: 평균 별점 계산, 중복 확인, 권한에 따른 업무 흐름 처리
- Repository: 검색 조건, 페이지네이션, 리뷰·찜 조회
- Template: Thymeleaf로 서버 데이터를 화면에 출력

**발표 멘트**

“계층을 분리해 화면 요청과 데이터 처리 책임을 나누었습니다. 예를 들어 상세 조회는 Controller가 PlaceService에서 맛집을 가져오고, ReviewService에서 리뷰를 가져온 뒤 평균 별점과 찜 정보를 조합해 Thymeleaf 화면에 전달합니다.”

### 페이지 3. ERD와 데이터 흐름

**화면 구성**

- 가운데 `BUSAN_RESTAURANTS`
- 좌우에 `REVIEW`, `BOOKMARK`
- 위에 `MEMBER`
- 아래에 독립적인 `MEMBER_LOG_DATA`

**설명할 데이터 흐름**

```text
맛집 1건 조회
  → restaurant_id로 리뷰 조회
  → 리뷰의 rating으로 평균 계산
  → restaurant_id로 찜 수 계산
  → 현재 member_id의 찜 여부 확인
  → Place 상세 화면에 통합 표시
```

**발표 멘트**

“맛집 테이블에 별점과 찜 수를 직접 저장하지 않고, 리뷰와 찜 테이블에서 계산합니다. 그래서 리뷰가 추가되거나 찜 상태가 바뀌면 상세 화면을 다시 조회할 때 최신 결과를 반영할 수 있습니다.”

### 페이지 4. 주요 사용자 화면

**화면별 한 줄 설명**

- 목록: 원하는 조건으로 후보 맛집을 좁히는 화면
- 상세: 방문 전에 필요한 정보를 확인하는 화면
- 지도: 주소를 실제 위치로 확인하는 화면
- 리뷰: 다른 방문자의 경험을 확인하는 화면
- 찜: 나중에 다시 볼 맛집을 저장하는 화면

**시연 포인트**

1. 지역구를 선택한다.
2. 검색 결과가 바뀌는 것을 보여준다.
3. 상세보기로 이동한다.
4. 평균 별점과 리뷰 수를 확인한다.
5. 지도의 마커와 주소를 보여준다.
6. 찜 버튼을 눌러 상태가 즉시 바뀌는 것을 보여준다.

### 페이지 5. 주요 관리자 화면

**화면 구성**

- 관리자 등록 화면: 입력 필드와 이미지 미리보기
- 관리자 대시보드: 회원 관리·리뷰 관리·동기화 메뉴
- 맛집 상세 화면: 관리자에게만 보이는 수정·삭제 버튼

**권한 설명**

화면에서 버튼을 숨기는 것과 서버에서 요청을 거부하는 것은 별개입니다. 이 프로젝트는 `PlaceController`, `AdminController`에서 세션의 `loginRole` 또는 DB의 회원 role을 확인합니다.

### 페이지 6. 공공데이터 동기화

**화면에 넣을 구현 단계**

```text
관리자 버튼 클릭
  → POST /admin/sync-opendata-all
  → 100건 단위 API 요청
  → JSON 응답 파싱
  → UC_SEQ 중복 확인
  → 상호명 + 주소 중복 확인
  → 신규 데이터 저장
  → 저장·중복·처리 건수 표시
```

**발표 멘트**

“동기화의 핵심은 매번 전체 데이터를 새로 넣지 않는 것입니다. 먼저 공공데이터의 UC_SEQ를 확인하고, 식별자가 없거나 기존 데이터와 비교가 필요한 경우 상호명과 주소를 함께 확인합니다. 중복은 건너뛰고 신규 데이터만 저장합니다.”

### 페이지 7. 마일리지와 보안

**마일리지 처리**

현재 버전에는 마일리지 테이블과 적립 로직이 없습니다. 발표에서 기능 목록에 포함해야 한다면 다음 단계 설계로 표현합니다.

```text
리뷰 등록 → 적립 정책 확인 → mileage_history 기록 → 회원 잔액 반영
```

**보안 처리**

- 화면에서 관리자 메뉴를 조건부 표시
- Controller에서 관리자 role 재검증
- 리뷰 수정·삭제 시 로그인 회원 번호와 작성자 번호 비교
- 찜·리뷰 등록 시 로그인 여부 확인
- OAuth2 로그인 후 회원 정보를 DB와 연결
- 활동 로그에 로그인·검색·접근 거부·맛집 작업 기록

### 페이지 8. 트러블 슈팅

문제 설명은 “문제 → 원인 → 해결 → 결과” 순서로 말하면 이해하기 쉽습니다.

**주소 검색 문제 예시**

- 문제: 등록된 주소 그대로는 카카오 지오코딩이 실패
- 원인: 괄호·층수·상가명이 포함된 비표준 주소
- 해결: 주소 정제 후 재검색, 실패 시 상호명 키워드 검색
- 결과: 지도 표시 성공률 개선

**공공데이터 문제 예시**

- 문제: 동기화를 다시 실행하면 중복 맛집이 생길 수 있음
- 원인: 외부 API는 같은 데이터를 반복해서 반환
- 해결: `apiId`, 상호명+주소 중복 검사
- 결과: 신규 데이터와 기존 데이터 구분

**삭제 정합성 문제 예시**

- 문제: 맛집 삭제 후 연결된 찜 데이터가 남을 수 있음
- 원인: 삭제 흐름에서 리뷰만 정리하고 찜 삭제 메서드는 호출하지 않음
- 해결 방향: 맛집 삭제 트랜잭션에 Bookmark 삭제 추가
- 결과 목표: 고아 찜 데이터 방지

### 페이지 9. 품질과 회고

**품질 슬라이드 구성**

- 자동 테스트 현황
- 핵심 수동 시나리오
- 현재 한계
- 다음 개선 작업

**마무리 멘트**

“Place 기능은 목록 화면 하나를 구현하는 작업에서 시작했지만, 외부 데이터, 권한, 사용자 리뷰, 지도, 찜, 관리자 운영까지 연결되는 기능으로 확장되었습니다. 다음 단계에서는 조회 성능과 데이터 무결성을 개선하고, 아직 구현되지 않은 마일리지 기능을 별도 설계하겠습니다.”

---

# 핵심 구현 발표본: 기능 흐름 중심 구성

아래 구성은 실제 발표에 사용할 본문입니다. 각 페이지에는 핵심 코드만 짧게 넣고, 나머지는 사용자가 보는 동작과 서버에서 처리되는 흐름을 설명합니다. 화면 캡처를 크게 배치하고 코드 블록은 작은 보조 영역으로 사용하는 구성을 권장합니다.

## 핵심 구현 1. 상세 페이지가 완성되는 과정

### 화면에 넣을 내용

```text
사용자 요청: /place/read?pno=12
        ↓
PlaceController.read()
        ↓
PlaceService: 맛집 기본 정보 조회
ReviewService: 전체·최신 리뷰 조회
BookmarkService: 찜 수·현재 회원 찜 여부 조회
        ↓
평균 별점 계산 + 화면 데이터 조합
        ↓
Thymeleaf 상세 화면 출력
```

### 핵심 코드만 표시

```java
PlaceDTO placeDTO = placeService.readOne(pno);
List<ReviewDTO> reviews =
        reviewService.getReviewsByRestaurant(pno);
placeDTO.setAvgRating(calculateAverage(reviews));
placeDTO.setBookmarkCount(
        bookmarkService.getBookmarkCount(pno));
```

### 설명할 내용

상세 페이지는 맛집 정보만 보여주는 페이지가 아닙니다. 여러 Service의 결과를 Controller에서 조합해 사용자가 방문 여부를 결정하는 데 필요한 정보를 한 번에 제공합니다. 별점과 찜 수를 별도 집계 데이터로 저장하지 않고 조회 시 계산하기 때문에 리뷰와 찜 변경이 즉시 반영됩니다.

## 핵심 구현 2. 검색과 페이지네이션

### 화면에 넣을 내용

지역구, 카테고리, 키워드 필터가 어떻게 SQL 조건으로 연결되는지 보여줍니다.

```text
검색 조건 입력
   ↓
PageRequestDTO
   ↓
PlaceServiceImpl.getList()
   ↓
PlaceRepository.searchList()
   ↓
PageResponseDTO로 결과·페이지 정보 반환
```

### 핵심 코드만 표시

```java
Page<Place> result = placeRepository.searchList(
        dto.getGuName(), dto.getCategory(),
        dto.getKeyword(), pageable);
```

### 설명할 내용

검색 조건이 비어 있으면 해당 조건을 적용하지 않고, 값이 있으면 지역구·카테고리·상호명·대표 메뉴·주소를 기준으로 검색합니다. 페이지 단위로 조회해 한 화면에 모든 데이터를 불러오지 않으며, 화면에는 전체 건수와 이전·다음 페이지 정보를 함께 제공합니다.

## 핵심 구현 3. 지도 위치 표시

### 화면에 넣을 내용

```text
원본 주소 검색
   ├─ 성공 → 좌표 변환 → 마커 표시
   └─ 실패 → 괄호·층수 제거 후 재검색
                    └─ 실패 → 지역구 + 상호명 검색
```

### 핵심 코드만 표시

```javascript
geocoder.addressSearch(rawAddress,
    function(result, status) {
        if (status === kakao.maps.services.Status.OK) {
            displayMarker(toLatLng(result), restaurantName);
        } else {
            searchByKeyword();
        }
    });
```

### 설명할 내용

공공데이터 주소는 괄호, 층수, 상가명 등으로 인해 검색 형식이 일정하지 않을 수 있습니다. 그래서 한 번 실패했다고 기능을 종료하지 않고 주소를 정제한 뒤 재검색하고, 마지막에는 상호명 기반 검색으로 보완합니다. 이 부분은 외부 API를 사용할 때 필요한 예외 처리 사례입니다.

## 핵심 구현 4. 찜하기 AJAX

### 화면에 넣을 내용

```text
찜 버튼 클릭
   ↓ POST /place/bookmark/toggle
로그인 여부 확인
   ↓
BookmarkService 토글
   ↓
찜 여부·찜 수 JSON 반환
   ↓
버튼 상태를 즉시 변경
```

### 핵심 코드만 표시

```javascript
fetch('/place/bookmark/toggle', {
    method: 'POST',
    body: 'restaurantId=' + restaurantId
});
```

```java
return ResponseEntity.ok(Map.of(
        "bookmarked", bookmarked,
        "count", count));
```

### 설명할 내용

페이지 전체를 새로고침하지 않고 버튼과 찜 수만 변경해 사용자 경험을 개선했습니다. 로그인하지 않은 경우 서버가 401을 반환하고 로그인 화면으로 안내합니다. DB에는 `(member_id, restaurant_id)` 유니크 제약을 두어 같은 맛집의 중복 찜을 방지합니다.

## 핵심 구현 5. 리뷰와 사용자 권한

### 화면에 넣을 내용

리뷰 작성 → 저장 → 상세 화면 재조회 → 평균 별점과 리뷰 수 갱신 흐름을 보여줍니다.

```text
로그인 사용자
   ↓ 별점·혼밥 레벨·내용·사진 입력
POST /place/review/add
   ↓
ReviewService 저장
   ↓
상세 페이지로 이동
   ↓
리뷰 목록과 평균 별점 갱신
```

### 핵심 코드만 표시

```java
Long memberNo = (Long) session
        .getAttribute("loginMember");
if (memberNo == null) {
    return "redirect:/member/login";
}
reviewService.addReview(
        restaurantId, memberNo, rating,
        soloLevel, content, file);
```

### 설명할 내용

리뷰에는 별점, 혼밥 난이도, 텍스트, 사진을 저장할 수 있습니다. 화면에서 작성·수정 버튼을 제한하는 것과 별개로 Controller에서도 로그인 회원과 작성자 번호를 확인합니다. 따라서 작성자 본인 또는 관리자만 리뷰를 수정·삭제할 수 있습니다.

## 핵심 구현 6. 공공데이터 동기화

### 화면에 넣을 내용

```text
관리자 동기화 요청
   ↓
부산 공공데이터 API를 100건 단위로 호출
   ↓
응답 상태·빈 데이터·오류 형식 확인
   ↓
UC_SEQ 중복 확인
   ↓
상호명 + 주소 중복 확인
   ↓
신규 데이터만 저장
```

### 핵심 코드만 표시

```java
if (placeRepository.findByApiId(apiId).isEmpty()
        && !placeRepository
        .existsByRestaurantNameKoAndAddress(name, address)) {
    placeRepository.save(place);
}
```

### 설명할 내용

동기화의 핵심은 외부 API를 다시 호출해도 중복 데이터가 생기지 않도록 하는 것입니다. API의 고유 번호인 `UC_SEQ`를 우선 비교하고, 고유 번호가 없으면 상호명과 주소를 함께 비교합니다. 전체 동기화 결과는 신규 저장 건수, 중복 건너뛴 건수, 전체 처리 건수로 관리자에게 보여줍니다.

## 핵심 구현 7. 관리자 등록과 보안

### 화면에 넣을 내용

```text
관리자 요청
   ↓ role 확인
맛집 정보·이미지 입력
   ↓
파일 UUID 저장 + 이미지 URL 매핑
   ↓
Place 저장
   ↓
상세 화면 이동
```

### 핵심 코드만 표시

```java
if (!"ROLE_ADMIN".equals(
        session.getAttribute("loginRole"))) {
    return "redirect:/place/list";
}
```

### 설명할 내용

관리자 메뉴를 화면에서 숨기는 것만으로는 충분하지 않기 때문에 등록·수정·삭제 요청마다 서버에서 권한을 다시 확인합니다. 이미지 파일은 UUID 파일명으로 저장해 파일명 충돌을 줄이고, 등록이 끝나면 생성된 식당 번호로 상세 화면에 이동합니다.

## 핵심 구현 8. 트러블 슈팅과 개선 과제

### 화면에 넣을 내용

| 문제 | 원인 | 해결 또는 개선 |
|---|---|---|
| 지도 검색 실패 | 주소 형식 불규칙 | 주소 정제와 상호명 재검색 |
| 공공데이터 중복 | API 재호출 | API ID, 상호명+주소 비교 |
| 목록 조회 증가 | 맛집마다 리뷰 집계 | 통합 조회 또는 집계 쿼리 검토 |
| 삭제 후 찜 잔존 가능성 | 찜 삭제 호출 누락 | Place 삭제 시 Bookmark도 정리 |
| 지도 재검색 비용 | 좌표 미저장 | 좌표 컬럼 추가 검토 |

### 설명할 내용

트러블 슈팅은 코드보다 문제 상황과 판단 과정을 중심으로 설명합니다. 특히 외부 데이터와 연관 데이터 삭제는 정상 동작만 확인해서는 부족하므로, 실패 상황과 데이터 정합성까지 함께 점검했다는 점을 강조합니다.

---

# 코드 상세 부록

아래 내용은 발표 중 코드 질문이 나왔을 때 보여주는 참고 자료입니다. 본문 슬라이드에는 핵심 코드만 사용하고, 전체 메서드와 세부 구현은 부록으로 남겨 발표 화면을 복잡하게 만들지 않습니다.

## 코드 페이지 1. 맛집 상세 조회: 여러 데이터를 하나의 화면으로 조합

### 상단 제목

**PLACE READ · 상세 정보 조회와 리뷰·찜 데이터 통합**

### 설명 문구

맛집 기본 정보뿐 아니라 리뷰 목록, 최신 리뷰 3개, 평균 별점, 리뷰 수, 찜 수, 현재 사용자의 찜 여부를 함께 조회해 상세 화면에 전달합니다.

### 화면 캡처

- `place/read.html`의 맛집 상세 카드
- 평균 별점과 리뷰 개수
- 찜하기 버튼
- 지도와 리뷰 영역

### 핵심 코드

```java
@GetMapping({"/read", "/modify"})
public void read(Long pno, PageRequestDTO pageRequestDTO,
                 HttpSession session, Model model) {
    PlaceDTO placeDTO = placeService.readOne(pno);

    List<ReviewDTO> reviews =
            reviewService.getReviewsByRestaurant(pno);
    List<ReviewDTO> topReviews =
            reviewService.getTop3ReviewsByRestaurant(pno);

    double avgRating = reviews.isEmpty() ? 0.0 :
            Math.round(reviews.stream()
                    .mapToInt(ReviewDTO::getRating)
                    .average().orElse(0.0) * 10.0) / 10.0;

    placeDTO.setAvgRating(avgRating);
    placeDTO.setReviewCount(reviews.size());
    placeDTO.setBookmarkCount(
            bookmarkService.getBookmarkCount(pno));

    Long memberNo = (Long) session.getAttribute("loginMember");
    placeDTO.setIsBookmarked(memberNo != null &&
            bookmarkService.isBookmarked(memberNo, pno));

    model.addAttribute("dto", placeDTO);
    model.addAttribute("reviews", reviews);
    model.addAttribute("topReviews", topReviews);
}
```

### 구현 포인트

- 맛집 원본 데이터는 `PlaceService`에서 조회
- 리뷰 데이터는 `ReviewService`에서 조회
- 평균 별점은 리뷰의 `rating`으로 계산
- 찜 수는 전체 개수, 찜 여부는 현재 회원 기준으로 계산
- Thymeleaf의 `dto`, `reviews`, `topReviews`로 화면에 전달

### 발표 멘트

“상세 화면은 Place 테이블 하나만 조회해서 끝내지 않습니다. 맛집 정보에 리뷰와 찜 데이터를 결합해야 사용자가 방문 여부를 판단할 수 있기 때문에 Controller에서 여러 Service의 결과를 조합해 하나의 화면 모델로 전달했습니다.”

---

## 코드 페이지 2. 맛집 검색: 동적 조건과 페이지네이션

### 상단 제목

**PLACE SEARCH · 지역·카테고리·키워드 조건 검색**

### 설명 문구

사용자가 입력한 검색 조건이 없으면 전체 맛집을 조회하고, 조건이 있으면 지역구·카테고리·상호명·대표 메뉴·주소를 기준으로 결과를 좁힙니다.

### 핵심 코드 1: Service에서 검색 요청 전달

```java
public PageResponseDTO<PlaceDTO> getList(PageRequestDTO dto) {
    Pageable pageable = dto.getPageable("pno");

    Page<Place> result = placeRepository.searchList(
            dto.getGuName(),
            dto.getCategory(),
            dto.getKeyword(),
            pageable
    );

    List<PlaceDTO> list = result.getContent().stream()
            .map(this::entityToDto)
            .collect(Collectors.toList());

    return PageResponseDTO.<PlaceDTO>withAll()
            .pageRequestDTO(dto)
            .dtoList(list)
            .total((int) result.getTotalElements())
            .build();
}
```

### 핵심 코드 2: Repository의 조건 검색

```java
@Query("SELECT p FROM Place p WHERE " +
       "(:guName IS NULL OR :guName = '' OR " +
       " p.guName = :guName) AND " +
       "(:category IS NULL OR :category = '' OR " +
       " p.category LIKE CONCAT('%', :category, '%')) AND " +
       "(:keyword IS NULL OR :keyword = '' OR " +
       " p.restaurantNameKo LIKE CONCAT('%', :keyword, '%') OR " +
       " p.restaurantNameEn LIKE CONCAT('%', :keyword, '%') OR " +
       " p.mainMenu LIKE CONCAT('%', :keyword, '%') OR " +
       " p.address LIKE CONCAT('%', :keyword, '%'))")
Page<Place> searchList(...);
```

### 구현 포인트

- `Pageable`로 한 페이지 12건을 조회
- `pno` 내림차순으로 최신 등록 데이터를 우선 표시
- 조건이 비어 있으면 해당 조건을 무시
- Entity를 DTO로 변환해 화면 계층과 DB 계층을 분리

### 발표 멘트

“검색 조건마다 별도의 SQL을 만들지 않고, 조건이 비어 있으면 무시하도록 JPQL을 구성했습니다. 이렇게 지역과 카테고리, 키워드를 조합해도 하나의 검색 메서드로 처리할 수 있습니다.”

---

## 코드 페이지 3. 카카오 지도: 주소 검색 실패에 대비한 재검색

### 상단 제목

**PLACE MAP · 주소 정제와 상호명 검색을 이용한 위치 표시**

### 설명 문구

주소 형식이 일정하지 않기 때문에 한 번의 검색 결과에 의존하지 않고, 주소 검색 실패 시 정제 주소와 상호명으로 순차 재검색합니다.

### 핵심 코드

```javascript
function searchLocation() {
    if (!rawAddress) {
        searchByKeyword();
        return;
    }

    geocoder.addressSearch(rawAddress, function(result, status) {
        if (status === kakao.maps.services.Status.OK) {
            const coords = new kakao.maps.LatLng(
                    result[0].y, result[0].x);
            displayMarker(coords, restaurantName);
        } else {
            let cleaned = rawAddress
                .replace(/\(.*?\)/g, "")
                .replace(/\s\d+층.*/g, "")
                .trim();

            if (cleaned && cleaned !== rawAddress) {
                geocoder.addressSearch(cleaned, ...);
            } else {
                searchByKeyword();
            }
        }
    });
}
```

### 처리 흐름

```text
원본 주소 검색
  ├─ 성공: 좌표 생성 + 마커 표시
  └─ 실패: 괄호·층수 제거 후 재검색
                └─ 실패: 부산 + 지역구 + 상호명 검색
```

### 발표 멘트

“공공데이터 주소는 괄호나 층수처럼 지도 검색에 불필요한 정보가 섞일 수 있습니다. 그래서 원본 주소, 정제 주소, 상호명 검색 순서로 대응해 외부 데이터의 불완전성을 보완했습니다.”

---

## 코드 페이지 4. 찜하기: AJAX로 화면 상태 즉시 변경

### 상단 제목

**BOOKMARK · POST 요청 후 버튼과 찜 수 즉시 갱신**

### 설명 문구

찜 버튼을 클릭하면 페이지 전체를 새로고침하지 않고 서버에 상태를 전달합니다. 서버 응답에 따라 버튼 문구, 색상, 찜 수를 즉시 바꿉니다.

### 핵심 코드 1: 화면의 fetch 요청

```javascript
fetch('/place/bookmark/toggle', {
    method: 'POST',
    headers: {
        'Content-Type':
            'application/x-www-form-urlencoded'
    },
    body: 'restaurantId=' + restaurantId
})
.then(response => {
    if (response.status === 401) {
        window.location.href = '/member/login';
        return null;
    }
    return response.json();
})
```

### 핵심 코드 2: Controller의 JSON 응답

```java
@PostMapping("/bookmark/toggle")
@ResponseBody
public ResponseEntity<?> toggleBookmark(
        @RequestParam Long restaurantId,
        HttpSession session) {
    Long memberNo =
            (Long) session.getAttribute("loginMember");

    if (memberNo == null) {
        return ResponseEntity.status(401)
                .body(Map.of("error", "LOGIN_REQUIRED"));
    }

    boolean bookmarked =
            bookmarkService.toggleBookmark(memberNo, restaurantId);
    long count = bookmarkService.getBookmarkCount(restaurantId);

    return ResponseEntity.ok(Map.of(
            "bookmarked", bookmarked,
            "count", count
    ));
}
```

### 발표 멘트

“찜하기는 사용자 경험을 위해 비동기로 처리했습니다. 서버는 현재 찜 여부와 전체 찜 수를 반환하고, 클라이언트는 응답을 받아 버튼만 갱신합니다. 비로그인 요청은 서버에서 401로 거부합니다.”

---

## 코드 페이지 5. 리뷰 등록과 권한 분리

### 상단 제목

**REVIEW · 별점·혼밥 레벨·사진을 포함한 방문 후기**

### 설명 문구

로그인 사용자는 맛집별로 리뷰를 작성할 수 있습니다. 리뷰는 별점, 혼밥 난이도, 텍스트, 사진을 저장하고, 작성자 본인 또는 관리자만 수정·삭제할 수 있습니다.

### 핵심 코드

```java
@PostMapping("/review/add")
public String addReview(
        @RequestParam Long restaurantId,
        @RequestParam(defaultValue = "5") Integer rating,
        @RequestParam(defaultValue = "LV1") SoloLevel soloLevel,
        @RequestParam String content,
        @RequestParam(value = "file", required = false)
        MultipartFile file,
        HttpSession session,
        RedirectAttributes redirectAttributes) {

    Long memberNo =
            (Long) session.getAttribute("loginMember");
    if (memberNo == null) {
        redirectAttributes.addFlashAttribute(
                "error", "로그인 후 리뷰를 작성할 수 있습니다.");
        return "redirect:/place/read?pno=" + restaurantId;
    }

    reviewService.addReview(
            restaurantId, memberNo, rating,
            soloLevel, content, file);
    return "redirect:/place/read?pno=" + restaurantId;
}
```

### 데이터 제약

```java
@UniqueConstraint(
    name = "uk_review_restaurant_member",
    columnNames = {"restaurant_id", "member_id"}
)
```

### 발표 멘트

“리뷰는 단순한 텍스트가 아니라 별점과 혼밥 난이도, 사진까지 포함합니다. 또한 DB 유니크 제약으로 한 회원이 같은 맛집에 리뷰를 중복 등록하지 못하게 했습니다.”

---

## 코드 페이지 6. 공공데이터 동기화: 중복 저장 방지

### 상단 제목

**OPEN API SYNC · 외부 데이터를 신규 맛집으로 변환·저장**

### 설명 문구

관리자 동기화 버튼으로 부산 공공데이터 API를 호출하고, 외부 필드를 Place Entity 필드로 변환합니다.

### 핵심 코드

```java
String apiId = item.path("UC_SEQ").asString("");
String name = item.path("MAIN_TITLE").asString("");
String guName = item.path("GUGUN_NM").asString("");
String address = item.path("ADDR1").asString("");
String mainMenu = item.path("RPRS_MENU").asString("");

boolean exists = !apiId.trim().isEmpty()
        && placeRepository.findByApiId(apiId.trim()).isPresent();

if (!exists) {
    exists = placeRepository
        .existsByRestaurantNameKoAndAddress(name, address);
}

if (!exists) {
    placeRepository.save(Place.builder()
        .restaurantNameKo(name)
        .guName(guName)
        .address(address)
        .mainMenu(mainMenu)
        .sourceType("API")
        .apiId(apiId)
        .build());
}
```

### 구현 포인트

- `UC_SEQ`를 `apiId`로 저장
- API ID가 없으면 상호명과 주소를 조합해 중복 확인
- API 응답 오류와 빈 응답을 별도 상태로 반환
- 전체 동기화는 100건 단위로 페이지를 순회
- 신규 저장 건수와 중복 건수를 관리자에게 반환

### 발표 멘트

“외부 API 동기화에서 가장 중요한 부분은 중복 방지입니다. API 고유 ID를 우선 비교하고, 고유 ID가 없을 때는 상호명과 주소를 함께 비교했습니다. 이 방식으로 동기화 버튼을 여러 번 눌러도 기존 맛집을 다시 쌓지 않도록 했습니다.”

---

## 코드 페이지 7. 관리자 맛집 등록: 이미지 업로드와 권한 확인

### 상단 제목

**ADMIN PLACE REGISTER · 관리자 전용 맛집 등록**

### 핵심 코드

```java
String role = (String) session.getAttribute("loginRole");
if (!"ROLE_ADMIN".equals(role)) {
    redirectAttributes.addFlashAttribute(
            "error", "관리자만 맛집을 등록할 수 있습니다.");
    return "redirect:/place/list";
}

if (image1File != null && !image1File.isEmpty()) {
    String uploadedUrl = saveUploadedImage(image1File);
    placeDTO.setImageUrl1(uploadedUrl);
}

Long pno = placeService.register(placeDTO);
return "redirect:/place/read?pno=" + pno;
```

### 구현 포인트

- 관리자 role을 서버에서 확인
- 이미지 파일은 UUID 파일명으로 저장해 이름 충돌 방지
- 저장 경로는 `upload.path` 설정으로 분리
- 저장된 파일 URL을 `PlaceDTO.imageUrl1`, `imageUrl2`에 매핑
- 등록 완료 후 생성된 맛집 상세 화면으로 이동

### 발표 멘트

“등록 화면에서는 맛집 정보와 이미지를 함께 관리합니다. 파일명은 UUID로 바꿔 저장하기 때문에 파일명이 겹쳐 기존 이미지가 덮어써지는 문제를 줄였습니다. 등록이 끝나면 생성된 식당 번호를 이용해 바로 상세 화면으로 이동합니다.”

---

## 코드 페이지 8. Place 삭제와 연관 데이터 정리

### 상단 제목

**PLACE REMOVE · 관리자 삭제와 연관 리뷰 정리**

### 핵심 코드

```java
public void remove(Long pno) {
    if (pno == null) return;

    // 현재 구현: 연관 리뷰 먼저 삭제
    reviewRepository.deleteByRestaurantId(pno);
    placeRepository.deleteById(pno);
}
```

### 발표 시 함께 설명할 점

- 맛집 삭제 전에 리뷰를 먼저 삭제하는 순서
- 현재 `BookmarkRepository`에는 찜 일괄 삭제 메서드가 있음
- 하지만 `PlaceServiceImpl.remove()`에서는 아직 호출하지 않음
- 따라서 다음 개선에서는 리뷰와 찜을 모두 삭제하도록 보완 필요

### 발표 멘트

“삭제 기능은 단순히 Place 행만 삭제하는 것이 아니라 연결된 데이터까지 고려해야 합니다. 현재는 리뷰 삭제 후 맛집을 삭제하는 흐름이며, 찜 데이터 정리 호출은 추가 보완이 필요합니다. 이 부분을 품질 개선 과제로 확인했습니다.”

---

## 예상 질문과 답변

### Q. 평균 별점은 어떻게 계산하나요?

상세 조회 시 해당 맛집의 리뷰를 가져온 뒤 별점 합계를 리뷰 개수로 나누고, 소수 첫째 자리까지 반올림합니다. 리뷰가 없으면 `0.0`으로 표시합니다.

### Q. 지도 위치가 정확하지 않을 때는 어떻게 하나요?

원본 주소 검색, 정제한 주소 검색, 지역구와 상호명 기반 키워드 검색 순서로 재시도합니다. 모든 검색이 실패하면 콘솔에 위치 탐색 실패를 기록합니다.

### Q. 일반 사용자가 맛집 정보를 수정할 수 있나요?

수정·삭제·신규 등록 요청에서 `ROLE_ADMIN` 여부를 확인하므로 일반 사용자는 이용할 수 없습니다.

### Q. 리뷰는 누구나 수정·삭제할 수 있나요?

로그인한 작성자 본인 또는 관리자만 수정·삭제할 수 있습니다.

## 참고 코드

- 상세 화면: `src/main/resources/templates/place/read.html`
- 리뷰 화면 조각: `src/main/resources/templates/place/fragments.html`
- Place 요청 처리: `src/main/java/com/example/matgipv1/controller/PlaceController.java`
- Place 비즈니스 로직: `src/main/java/com/example/matgipv1/service/PlaceServiceImpl.java`
- 검색 Repository: `src/main/java/com/example/matgipv1/repository/PlaceRepository.java`
