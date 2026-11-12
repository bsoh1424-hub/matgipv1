package com.example.matgipv1.service;

import com.example.matgipv1.domain.LogData;
import com.example.matgipv1.repository.LogDataRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
public class LogDataService {

    private final LogDataRepository logDataRepository;

    /**
     * 기본 활동 로그 기록 메서드
     */
    @Transactional
    public void saveLog(LogData logData) {
        try {
            logDataRepository.save(logData);
        } catch (Exception e) {
            log.error("로그 데이터 저장 실패: {}", e.getMessage(), e);
        }
    }

    /**
     * HTTP Request 정보를 바탕으로 간편하게 회원 활동 로그를 기록하는 메서드
     */
    @Transactional
    public void logActivity(
            HttpServletRequest request,
            String actionType,
            String description,
            Long targetId,
            String targetName,
            String searchKeyword,
            String extraDetails
    ) {
        try {
            Long memberId = null;
            String email = null;
            String nickname = null;
            String role = "GUEST";

            if (request != null) {
                HttpSession session = request.getSession(false);
                if (session != null) {
                    memberId = (Long) session.getAttribute("loginMember");
                    nickname = (String) session.getAttribute("nickname");
                    role = (String) session.getAttribute("loginRole");
                    if (role == null) role = "GUEST";
                }
            }

            String ipAddress = getClientIp(request);
            String userAgent = request != null ? request.getHeader("User-Agent") : null;
            String referer = request != null ? request.getHeader("Referer") : null;
            String requestUrl = request != null ? request.getRequestURI() : null;
            String httpMethod = request != null ? request.getMethod() : null;
            String deviceType = detectDeviceType(userAgent);

            LogData logData = LogData.builder()
                    .memberId(memberId)
                    .email(email)
                    .nickname(nickname)
                    .role(role)
                    .actionType(actionType)
                    .description(description)
                    .requestUrl(requestUrl)
                    .httpMethod(httpMethod)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .deviceType(deviceType)
                    .referer(referer)
                    .targetId(targetId)
                    .targetName(targetName)
                    .searchKeyword(searchKeyword)
                    .extraDetails(extraDetails)
                    .statusCode(200)
                    .build();

            logDataRepository.save(logData);
        } catch (Exception e) {
            log.error("활동 로그 기록 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    /**
     * 프록시 및 로드밸런서를 고려한 클라이언트 실제 IP 추출
     */
    public static String getClientIp(HttpServletRequest request) {
        if (request == null) return "UNKNOWN";
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * User-Agent 문자열을 기반으로 간단한 기기 유형(PC, Mobile, Tablet) 분류
     */
    private String detectDeviceType(String userAgent) {
        if (userAgent == null) return "Unknown";
        String ua = userAgent.toLowerCase();
        if (ua.contains("ipad") || ua.contains("tablet") || ua.contains("playbook")) {
            return "Tablet";
        } else if (ua.contains("mobile") || ua.contains("android") || ua.contains("iphone") || ua.contains("ipod")) {
            return "Mobile";
        }
        return "PC";
    }
}
