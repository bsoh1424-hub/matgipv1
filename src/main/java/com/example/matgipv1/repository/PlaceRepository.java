package com.example.matgipv1.repository;

import com.example.matgipv1.domain.Place;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    // apiId로 기존 등록된 데이터가 있는지 확인하는 메소드
    Optional<Place> findByApiId(String apiId);

    // 식당 이름과 주소로 기존 데이터가 있는지 확인하는 메소드
    boolean existsByRestaurantNameKoAndAddress(String restaurantNameKo, String address);

    // guName이 없거나 빈값이면 전체 조회, 있으면 해당 guName만 조회
    @Query("SELECT p FROM Place p WHERE (:guName IS NULL OR :guName = '' OR p.guName = :guName) AND" +
    "(:category IS NULL OR :category='' OR p.category LIKE CONCAT('%', :category, '%')) AND" +
            "(:keyword IS NULL OR :keyword = '' OR " +
            " p.restaurantNameKo LIKE CONCAT('%', :keyword, '%') OR " +
            " p.restaurantNameEn LIKE CONCAT('%', :keyword, '%') OR " +
            " p.mainMenu LIKE CONCAT('%', :keyword, '%') OR " +
            " p.address LIKE CONCAT('%', :keyword, '%'))")
    Page<Place> searchList(@Param("guName") String guName, @Param("category") String category, @Param("keyword") String keyword, Pageable pageable);

    // 오늘 뭐 먹지? (랜덤 가챠용 조건 조회)
    @Query("SELECT p FROM Place p WHERE " +
           "(:guName IS NULL OR :guName = '' OR p.guName LIKE CONCAT('%', :guName, '%')) AND " +
           "(:category IS NULL OR :category = '' OR p.category LIKE CONCAT('%', :category, '%'))")
    List<Place> findForGacha(@Param("guName") String guName, @Param("category") String category);
}
