package com.example.matgipv1.domain;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "busan_restaurants")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class Place extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long pno; // PK (자동 증가)

    @Column(name = "gu_name", length = 50, nullable = false)
    private String guName; // 구/군 이름

    @Column(name = "restaurant_name_ko", length = 100, nullable = false)
    private String restaurantNameKo; // 상호명 (한글)

    @Column(name = "restaurant_name_en", length = 100)
    private String restaurantNameEn; // 상호명 (영문)

    @Column(length = 50)
    private String category; // 카테고리

    @Column(name = "summary_ko", length = 2000)
    private String summaryKo; // 국문 한 줄 소개 / 요약

    @Column(name = "summary_en", length = 2000)
    private String summaryEn; // 영문 한 줄 소개 / 요약

    @Column(name = "image_url1", length = 500)
    private String imageUrl1; // 대표 이미지 URL 1

    @Column(name = "image_url2", length = 500)
    private String imageUrl2; // 대표 이미지 URL 2

    @Column(length = 255)
    private String address; // 상세 주소

    @Column(length = 30)
    private String phone; // 전화번호

    @Column(name = "operating_hours", length = 250)
    private String operatingHours; // 영업시간

    @Column(name = "break_time", length = 100)
    private String breakTime; // 브레이크 타임

    @Column(name = "off_days", length = 100)
    private String offDays; // 휴무일

    @Column(name = "main_menu", length = 255)
    private String mainMenu; // 대표 메뉴

    @Column(name = "price_range", length = 100)
    private String priceRange; // 대표 가격대

    @Column(length = 100)
    private String parking; // 주차 정보

    @Column(name = "page_num")
    private Integer pageNum; // 책 수록 페이지 번호

    @Column(name = "source_type", length = 20)
    private String sourceType;

    @Column(name = "api_id", length = 50)
    private String apiId;

    // 정보 수정을 위한 변경 메서드
    public void change(
            String guName,
            String restaurantNameKo,
            String restaurantNameEn,
            String category,
            String summaryKo,
            String summaryEn,
            String imageUrl1,
            String imageUrl2,
            String address,
            String phone,
            String operatingHours,
            String breakTime,
            String offDays,
            String mainMenu,
            String priceRange,
            String parking,
            Integer pageNum
    ) {
        this.guName = guName;
        this.restaurantNameKo = restaurantNameKo;
        this.restaurantNameEn = restaurantNameEn;
        this.category = category;
        this.summaryKo = summaryKo;
        this.summaryEn = summaryEn;
        this.imageUrl1 = imageUrl1;
        this.imageUrl2 = imageUrl2;
        this.address = address;
        this.phone = phone;
        this.operatingHours = operatingHours;
        this.breakTime = breakTime;
        this.offDays = offDays;
        this.mainMenu = mainMenu;
        this.priceRange = priceRange;
        this.parking = parking;
        this.pageNum = pageNum;
    }
}
