package com.example.matgipv1.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "member_log_data", indexes = {
        @Index(name = "idx_log_member_id", columnList = "member_id"),
        @Index(name = "idx_log_action_type", columnList = "action_type"),
        @Index(name = "idx_log_reg_date", columnList = "regdate")
})
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LogData extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long id; // 로그 PK (자동 증가)

    // ==========================================
    // 1. 회원 정보 (비로그인 방문자의 경우 null)
    // ==========================================
    @Column(name = "member_id")
    private Long memberId; // 회원 번호 (Member mno)

    @Column(name = "email", length = 100)
    private String email; // 회원 이메일 (또는 소셜 식별 이메일)

    @Column(name = "nickname", length = 50)
    private String nickname; // 회원 닉네임

    @Column(name = "role", length = 30)
    private String role; // 회원 권한 (ROLE_USER, ROLE_ADMIN, GUEST 등)

    // ==========================================
    // 2. 활동 및 이벤트 정보
    // ==========================================
    @Column(name = "action_type", length = 50, nullable = false)
    private String actionType; 
    // 예: LOGIN_LOCAL, LOGIN_KAKAO, LOGIN_NAVER, LOGOUT, AUTO_LOGOUT, 
    //     VIEW_PLACE, SEARCH_PLACE, ADD_REVIEW, REMOVE_REVIEW, 
    //     MODIFY_PLACE, REMOVE_PLACE, SYNC_OPENAPI, WITHDRAW, ACCESS_DENIED

    @Column(name = "description", length = 500)
    private String description; // 활동 요약 설명

    // ==========================================
    // 3. HTTP 웹 요청 및 네트워크 정보
    // ==========================================
    @Column(name = "request_url", length = 500)
    private String requestUrl; // 요청 URI 및 경로

    @Column(name = "http_method", length = 10)
    private String httpMethod; // GET, POST, PUT, DELETE 등

    @Column(name = "ip_address", length = 50)
    private String ipAddress; // 클라이언트 IP 주소 (X-Forwarded-For 포함)

    @Column(name = "user_agent", length = 500)
    private String userAgent; // 브라우저 및 OS 기기 정보 (User-Agent)

    @Column(name = "device_type", length = 30)
    private String deviceType; // PC, Mobile, Tablet 등

    @Column(name = "referer", length = 500)
    private String referer; // 이전 유입 페이지 URL

    // ==========================================
    // 4. 대상 맛집/리뷰 및 검색 파라미터 정보
    // ==========================================
    @Column(name = "target_id")
    private Long targetId; // 대상 맛집 PNO 또는 리뷰 ID

    @Column(name = "target_name", length = 100)
    private String targetName; // 대상 맛집 상호명

    @Column(name = "search_keyword", length = 255)
    private String searchKeyword; // 검색어 / 지역구 / 카테고리 필터값

    @Column(name = "extra_details", length = 3000)
    private String extraDetails; // 추가 JSON 데이터, 파라미터 상세 또는 에러 메시지

    // ==========================================
    // 5. 응답 결과 및 성능 지표
    // ==========================================
    @Column(name = "status_code")
    private Integer statusCode; // HTTP 상태 코드 (200, 302, 403, 500 등)

    @Column(name = "execution_time_ms")
    private Long executionTimeMs; // 서버 처리 소요 시간 (밀리초)
}
