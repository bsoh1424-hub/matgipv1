package com.example.matgipv1.repository;

import com.example.matgipv1.domain.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByRestaurantIdAndMemberId(Long restaurantId, Long memberId);

    // 회원별 작성 리뷰 최신순 조회 (마이페이지용)
    List<Review> findByMemberIdOrderByRegDateDesc(Long memberId);

    // 식당별 전체 리뷰 최신순 조회
    List<Review> findByRestaurantIdOrderByRegDateDesc(Long restaurantId);

    // [수정] 사용자 상세 화면에는 관리자가 숨김 처리한 리뷰를 제외합니다.
    @Query("SELECT r FROM Review r WHERE r.restaurantId = :restaurantId " +
            "AND (r.hidden = false OR r.hidden IS NULL) ORDER BY r.regDate DESC")
    List<Review> findVisibleByRestaurantIdOrderByRegDateDesc(@Param("restaurantId") Long restaurantId);

    // [수정] 최신 리뷰 3건도 숨김 처리된 리뷰를 제외합니다.
    @Query("SELECT r FROM Review r WHERE r.restaurantId = :restaurantId " +
            "AND (r.hidden = false OR r.hidden IS NULL) ORDER BY r.regDate DESC")
    List<Review> findVisibleReviewsByRestaurantIdOrderByRegDateDesc(@Param("restaurantId") Long restaurantId);

    // 식당별 최신 리뷰 3개 조회
    List<Review> findTop3ByRestaurantIdOrderByRegDateDesc(Long restaurantId);

    // 시스템 전체 리뷰 최신순 조회 (관리자용)
    List<Review> findAllByOrderByRegDateDesc();

    // 식당 삭제 시 연관 리뷰 일괄 삭제
    @Modifying
    @Query("DELETE FROM Review r WHERE r.restaurantId = :restaurantId")
    void deleteByRestaurantId(@Param("restaurantId") Long restaurantId);
}
