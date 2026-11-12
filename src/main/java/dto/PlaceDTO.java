package dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlaceDTO {
    private Long pno;
    private String guName;
    private String restaurantNameKo;
    private String restaurantNameEn;
    private String category;
    private String summaryKo;
    private String summaryEn;
    private String imageUrl1;
    private String imageUrl2;
    private String address;
    private String phone;
    private String operatingHours;
    private String breakTime;
    private String offDays;
    private String mainMenu;
    private String priceRange;
    private String parking;
    private Integer pageNum;

    // 통계 및 사용자 상태 필드
    private Double avgRating;         // 평균 별점 (예: 4.5)
    private Integer reviewCount;      // 리뷰 개수
    private Long bookmarkCount;       // 찜한 총 사용자 수
    private Boolean isBookmarked;     // 현재 사용자의 찜 여부

    private LocalDateTime regDate;
    private LocalDateTime modDate;
}
