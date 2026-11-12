package com.example.matgipv1.repository;

import com.example.matgipv1.domain.LogData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LogDataRepository extends JpaRepository<LogData, Long> {

    // 특정 회원의 로그 목록 (최신순)
    List<LogData> findByMemberIdOrderByRegDateDesc(Long memberId);

    // 특정 회원의 페이징된 로그 목록
    Page<LogData> findByMemberIdOrderByRegDateDesc(Long memberId, Pageable pageable);

    // 특정 액션 타입(예: LOGIN, ADD_REVIEW 등)별 로그 조회
    Page<LogData> findByActionTypeOrderByRegDateDesc(String actionType, Pageable pageable);

    // 회원 검색 및 액션타입 검색 쿼리
    @Query("SELECT l FROM LogData l WHERE " +
           "(:memberId IS NULL OR l.memberId = :memberId) AND " +
           "(:actionType IS NULL OR :actionType = '' OR l.actionType = :actionType) AND " +
           "(:keyword IS NULL OR :keyword = '' OR l.email LIKE CONCAT('%', :keyword, '%') OR l.nickname LIKE CONCAT('%', :keyword, '%') OR l.targetName LIKE CONCAT('%', :keyword, '%') OR l.description LIKE CONCAT('%', :keyword, '%')) " +
           "ORDER BY l.regDate DESC")
    Page<LogData> searchLogs(@Param("memberId") Long memberId,
                             @Param("actionType") String actionType,
                             @Param("keyword") String keyword,
                             Pageable pageable);
}
