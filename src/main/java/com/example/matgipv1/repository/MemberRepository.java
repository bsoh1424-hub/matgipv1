package com.example.matgipv1.repository;

import com.example.matgipv1.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    // 이메일로 회원 정보 조회
    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);

    // 회원번호로 직접 DB 레코드 영구 삭제
    @Modifying
    @Query("DELETE FROM Member m WHERE m.mno = :mno")
    void deleteByMno(@Param("mno") Long mno);
}
