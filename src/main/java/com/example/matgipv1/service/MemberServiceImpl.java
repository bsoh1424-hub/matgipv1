package com.example.matgipv1.service;

import com.example.matgipv1.domain.Bookmark;
import com.example.matgipv1.domain.Member;
import com.example.matgipv1.domain.review.Review;
import com.example.matgipv1.repository.BookmarkRepository;
import com.example.matgipv1.repository.MemberRepository;
import com.example.matgipv1.repository.ReviewRepository;
import dto.MemberJoinDTO;
import dto.MemberLoginDTO;
import dto.MyPageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class MemberServiceImpl implements MemberService, UserDetailsService {

    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;
    private final BookmarkRepository bookmarkRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${upload.path:C:/upload/matgipv1}")
    private String uploadPath;

    // ==========================
    // 회원가입
    // ==========================
    @Override
    public Long join(MemberJoinDTO dto) {

        if (memberRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalStateException("이미 가입된 이메일입니다.");
        }

        Member member = Member.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .nickname(dto.getNickname())
                .role("ROLE_USER")
                .build();

        return memberRepository.save(member).getMno();
    }

    // ==========================
    // 로그인
    // ==========================
    @Override
    @Transactional(readOnly = true)
    public Member login(MemberLoginDTO dto) {

        return memberRepository.findByEmail(dto.getEmail())
                .filter(member ->
                        passwordEncoder.matches(
                                dto.getPassword(),
                                member.getPassword()
                        )
                )
                .orElse(null);
    }

    // ==========================
    // 마이페이지 조회
    // ==========================
    @Override
    @Transactional(readOnly = true)
    public MyPageDTO getMyPage(Long mno) {

        Member member = memberRepository.findById(mno)
                .orElseThrow(() ->
                        new IllegalArgumentException("회원 정보가 없습니다.")
                );

        return MyPageDTO.builder()
                .mno(member.getMno())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .profileImage(member.getProfileImage())
                .build();
    }

    // ==========================
    // 회원정보 수정
    // ==========================
    @Override
    public void modify(
            Long mno,
            String nickname,
            String newPassword,
            String profileImage
    ) {

        Member member = memberRepository.findById(mno)
                .orElseThrow(() ->
                        new IllegalArgumentException("회원 정보가 없습니다.")
                );

        // 닉네임 / 프로필 수정
        member.changeProfile(
                nickname,
                profileImage
        );

        // 비밀번호를 입력했을 때만 변경
        if (newPassword != null && !newPassword.isBlank()) {
            String encodedPassword = passwordEncoder.encode(newPassword);
            member.changePassword(encodedPassword);
        }
    }

    // ==========================
    // 회원탈퇴 (member 테이블에서 실제 조회 후 완전 삭제)
    // ==========================
    @Override
    public void withdraw(Long mno) {

        Member member = memberRepository.findById(mno)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않거나 이미 탈퇴된 회원입니다. (mno: " + mno + ")")
                );

        log.info(">>> [회원 탈퇴 진행] mno: {}, email: {}", mno, member.getEmail());

        // 1. 회원이 작성한 리뷰 및 첨부 사진 파일 정리
        try {
            List<Review> userReviews = reviewRepository.findByMemberIdOrderByRegDateDesc(mno);
            for (Review r : userReviews) {
                if (r.getStoreImageName() != null && !r.getStoreImageName().isBlank()) {
                    try {
                        Path filePath = Paths.get(uploadPath, r.getStoreImageName());
                        Files.deleteIfExists(filePath);
                    } catch (Exception ignored) {
                    }
                }
            }
            if (!userReviews.isEmpty()) {
                reviewRepository.deleteAll(userReviews);
            }
        } catch (Exception e) {
            log.warn("리뷰 정리 중 예외 발생 (계속 진행): {}", e.getMessage());
        }

        // 2. 회원의 찜(북마크) 데이터 일괄 삭제
        try {
            List<Bookmark> userBookmarks = bookmarkRepository.findByMemberIdOrderByRegDateDesc(mno);
            if (!userBookmarks.isEmpty()) {
                bookmarkRepository.deleteAll(userBookmarks);
            }
        } catch (Exception e) {
            log.warn("북마크 정리 중 예외 발생 (계속 진행): {}", e.getMessage());
        }

        // 3. 실제 회원 엔티티(DB member 테이블)에서 완전 삭제
        memberRepository.deleteByMno(mno);
        memberRepository.flush();
        log.info(">>> [회원 탈퇴 완료] member 테이블에서 영구 삭제 완료 - mno: {}", mno);
    }

    // ==========================
    // 이메일로 회원 찾기
    // ==========================
    @Override
    @Transactional(readOnly = true)
    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    // ==========================
    // Spring Security 로그인
    // ==========================
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "사용자를 찾을 수 없습니다: " + email
                        )
                );

        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .authorities(
                        Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_USER")
                        )
                )
                .build();
    }
}
