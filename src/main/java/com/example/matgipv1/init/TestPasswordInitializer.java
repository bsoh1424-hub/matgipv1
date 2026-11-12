package com.example.matgipv1.init;

import com.example.matgipv1.domain.Member;
import com.example.matgipv1.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestPasswordInitializer implements CommandLineRunner {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        Member member = memberRepository
                .findByEmail("admin@busantasty.com")
                .orElseThrow(() -> new RuntimeException("해당 회원이 없습니다."));

        member.changePassword(
                passwordEncoder.encode("1234")
        );

        memberRepository.save(member);

        System.out.println("=================================");
        System.out.println("테스트 계정 비밀번호 변경 완료");
        System.out.println("이메일: admin@busantasty.com");
        System.out.println("비밀번호: 1234");
        System.out.println("=================================");
    }
}
