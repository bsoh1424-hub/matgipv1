package com.example.matgipv1.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="member")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_no")
    private Long mno;

    private String providerId;  // 카카오 회원번호 또는 이메일 (AUTH-02)
    @Column(nullable = false, unique = true, length = 100)
    private String email;       // 선택사항 (AUTH-02)
    @Column(nullable = false)
    private String password;    // BCrypt 암호화 (AUTH-03)
    @Column(nullable = false, length=50)
    private String nickname;

    private String profileImage;

    //@Enumerated(EnumType.STRING)
    //private Role role;          // ROLE_USER, ROLE_ADMIN (AUTH-07)
    @Column(name = "role")
    private String role;

    // 🟢 [소셜 로그인용 추가 필드]
    @Builder.Default
    @Column(length = 20)
    private String provider = "LOCAL"; // LOCAL, NAVER 등

    @Column(name = "provider_id")

    // 닉네임 / 프로필 이미지 수정
    public void changeProfile(String nickname, String profileImage) {

        this.nickname = nickname;

        if (profileImage != null && !profileImage.isBlank()) {
            this.profileImage = profileImage;
        }
    }

    // 비밀번호 변경
    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    // 🟢 getRole() 메서드는 비즈니스 로직(메서드) 영역에 두기!
    public String getRole() {
        return role;
    }

    // Member.java 클래스 내부 적당한 곳에 추가 관리자페이지
    public void changeRole(String role) {
        this.role = role;
    }
}
