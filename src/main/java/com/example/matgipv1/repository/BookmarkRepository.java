package com.example.matgipv1.repository;

import com.example.matgipv1.domain.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    // 특정 회원이 특정 식당을 찜했는지 여부
    boolean existsByMemberIdAndRestaurantId(Long memberId, Long restaurantId);

    // 단건 조회
    Optional<Bookmark> findByMemberIdAndRestaurantId(Long memberId, Long restaurantId);

    // 회원의 전체 찜 목록 (최신순)
    List<Bookmark> findByMemberIdOrderByRegDateDesc(Long memberId);

    // 찜 해제 (단건 삭제)
    void deleteByMemberIdAndRestaurantId(Long memberId, Long restaurantId);

    // 식당별 총 찜 개수
    long countByRestaurantId(Long restaurantId);

    // 식당 삭제 시 연관 찜 데이터 일괄 삭제
    @Modifying
    @Query("DELETE FROM Bookmark b WHERE b.restaurantId = :restaurantId")
    void deleteByRestaurantId(@Param("restaurantId") Long restaurantId);
}
