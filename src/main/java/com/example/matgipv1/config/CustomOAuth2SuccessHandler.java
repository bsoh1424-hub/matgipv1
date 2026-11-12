package com.example.matgipv1.config;

import com.example.matgipv1.domain.LogData;
import com.example.matgipv1.domain.Member;
import com.example.matgipv1.repository.MemberRepository;
import com.example.matgipv1.service.LogDataService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Log4j2
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final MemberRepository memberRepository;
    private final LogDataService logDataService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        log.info("OAuth2 Success Handler 실행 - Attributes: {}", attributes);

        String email = null;
        String provider = "OAUTH2";

        // Naver
        if (attributes.get("response") != null) {
            provider = "NAVER";
            Map<String, Object> naverResp = (Map<String, Object>) attributes.get("response");
            email = (String) naverResp.get("email");
            if (email == null || email.isBlank()) {
                email = "naver_" + naverResp.get("id") + "@naver.social";
            }
        }
        // Kakao
        else if (attributes.get("id") != null) {
            provider = "KAKAO";
            String providerId = String.valueOf(attributes.get("id"));
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            if (kakaoAccount != null && kakaoAccount.get("email") != null) {
                email = String.valueOf(kakaoAccount.get("email"));
            }
            if (email == null || email.isBlank()) {
                email = "kakao_" + providerId + "@kakao.social";
            }
        }

        if (email != null) {
            Member member = memberRepository.findByEmail(email).orElse(null);
            if (member != null) {
                HttpSession session = request.getSession();
                session.setAttribute("loginMember", member.getMno());
                session.setAttribute("nickname", member.getNickname());
                session.setAttribute("loginRole", member.getRole());
                log.info("OAuth2 세션 설정 완료: mno={}, email={}, nickname={}, role={}",
                        member.getMno(), member.getEmail(), member.getNickname(), member.getRole());

                // 📝 멤버 로그인 활동 로그를 LogData 테이블에 자동 저장
                try {
                    String ip = LogDataService.getClientIp(request);
                    String userAgent = request.getHeader("User-Agent");
                    String referer = request.getHeader("Referer");

                    LogData logData = LogData.builder()
                            .memberId(member.getMno())
                            .email(member.getEmail())
                            .nickname(member.getNickname())
                            .role(member.getRole())
                            .actionType("LOGIN_" + provider)
                            .description(provider + " 소셜 로그인 성공")
                            .requestUrl(request.getRequestURI())
                            .httpMethod(request.getMethod())
                            .ipAddress(ip)
                            .userAgent(userAgent)
                            .referer(referer)
                            .statusCode(200)
                            .build();

                    logDataService.saveLog(logData);
                } catch (Exception e) {
                    log.warn("로그인 로그 적재 중 오류 (무시 가능): {}", e.getMessage());
                }
            } else {
                log.warn("OAuth2 사용자 조회 실패 - email: {}", email);
            }
        }

        response.sendRedirect("/place/list");
    }
}
