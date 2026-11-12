package com.example.matgipv1.config;

import com.example.matgipv1.domain.Member;
import com.example.matgipv1.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Log4j2
public class SecurityConfig {
    private final MemberRepository memberRepository;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    private final CustomOAuth2FailureHandler customOAuth2FailureHandler;

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService
    ) throws Exception {

        http
                // 1. CSRF 비활성화
                .csrf(csrf -> csrf.disable())

                // 2. URL 접근 권한 설정 (/upload/** 정적 업로드 사진 허용 추가)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/place/**", "/member/**", "/admin/**", "/upload/**", "/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                        .requestMatchers("/api/**").permitAll()
                        .anyRequest().authenticated()
                )

                // 3. OAuth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/member/login")
                        .successHandler(customOAuth2SuccessHandler)
                        .failureHandler(customOAuth2FailureHandler)
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                )

                // 4. 예외 처리
                .exceptionHandling(ex -> ex
                        .defaultAuthenticationEntryPointFor(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                request -> request.getRequestURI().startsWith("/api/")
                        )
                );

        return http.build();
    }

    // 비밀번호 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // OAuth2 사용자 정보 처리 서비스
    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService(PasswordEncoder passwordEncoder) {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

        return userRequest -> {
            OAuth2User oAuth2User = delegate.loadUser(userRequest);
            String registrationId = userRequest.getClientRegistration().getRegistrationId();
            String userNameAttributeName = userRequest.getClientRegistration()
                    .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

            Map<String, Object> attributes = oAuth2User.getAttributes();
            log.info("OAuth2 User Attributes [{}]: {}", registrationId, attributes);

            String providerId;
            String email;
            String nickname = "사용자";
            String profileImage = null;

            // NAVER
            if ("naver".equalsIgnoreCase(registrationId)) {
                Map<String, Object> naverResponse = (Map<String, Object>) attributes.get("response");
                if (naverResponse == null) {
                    throw new IllegalStateException("네이버 response 데이터가 없습니다.");
                }

                providerId = String.valueOf(naverResponse.get("id"));
                Object emailObject = naverResponse.get("email");
                Object nicknameObject = naverResponse.get("nickname");
                Object profileImageObject = naverResponse.get("profile_image");

                email = emailObject != null ? String.valueOf(emailObject) : null;
                nickname = nicknameObject != null ? String.valueOf(nicknameObject) : "네이버사용자";
                profileImage = profileImageObject != null ? String.valueOf(profileImageObject) : null;

                if (email == null || email.isBlank()) {
                    email = "naver_" + providerId + "@naver.social";
                }
            }
            // KAKAO
            else if ("kakao".equalsIgnoreCase(registrationId)) {
                providerId = String.valueOf(attributes.get("id"));
                Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
                Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

                if (properties != null) {
                    if (properties.get("nickname") != null) {
                        nickname = String.valueOf(properties.get("nickname"));
                    }
                    if (properties.get("profile_image") != null) {
                        profileImage = String.valueOf(properties.get("profile_image"));
                    }
                }
                if ((nickname == null || "사용자".equals(nickname)) && kakaoAccount != null) {
                    if (kakaoAccount.get("profile") != null) {
                        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                        if (profile.get("nickname") != null) {
                            nickname = String.valueOf(profile.get("nickname"));
                        }
                        if (profile.get("profile_image_url") != null) {
                            profileImage = String.valueOf(profile.get("profile_image_url"));
                        }
                    }
                }
                if (nickname == null || nickname.isBlank()) {
                    nickname = "카카오_" + providerId;
                }

                email = null;
                if (kakaoAccount != null && kakaoAccount.get("email") != null) {
                    email = String.valueOf(kakaoAccount.get("email"));
                }
                if (email == null || email.isBlank()) {
                    email = "kakao_" + providerId + "@kakao.social";
                }
            } else {
                throw new IllegalStateException("지원하지 않는 OAuth Provider: " + registrationId);
            }

            // 회원 DB 저장 및 조회 (신규 회원이면 등록, 기존 회원이면 조회)
            final String finalEmail = email;
            final String finalNickname = nickname;
            final String finalProfileImage = profileImage;
            final String finalProvider = registrationId.toUpperCase();
            final String finalProviderId = providerId;

            Member member = memberRepository.findByEmail(finalEmail)
                    .orElseGet(() -> memberRepository.save(
                            Member.builder()
                                    .email(finalEmail)
                                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                                    .nickname(finalNickname)
                                    .profileImage(finalProfileImage)
                                    .role("ROLE_USER")
                                    .provider(finalProvider)
                                    .providerId(finalProviderId)
                                    .build()
                    ));

            log.info("OAuth2 회원 연동 성공: mno={}, email={}, nickname={}, provider={}",
                    member.getMno(), member.getEmail(), member.getNickname(), member.getProvider());

            return new DefaultOAuth2User(
                    Collections.singleton(new SimpleGrantedAuthority(member.getRole())),
                    attributes,
                    userNameAttributeName
            );
        };
    }
}
