package com.example.matgipv1.service;

import com.example.matgipv1.domain.Member;
import dto.MemberJoinDTO;
import dto.MemberLoginDTO;
import dto.MyPageDTO;

import java.util.Optional;

public interface MemberService {

    // 회원가입
    Long join(MemberJoinDTO memberJoinDTO);

    // 로그인
    Member login(MemberLoginDTO memberLoginDTO);

    // 마이페이지 조회
    MyPageDTO getMyPage(Long mno);

    // 회원정보 수정
    void modify(
            Long mno,
            String nickname,
            String newPassword,
            String profileImage
    );

    // 회원탈퇴
    void withdraw(Long mno);

    // 이메일로 회원 찾기
    Optional<Member> findByEmail(String email);
}