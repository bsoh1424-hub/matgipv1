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
public class BookmarkDTO {
    private Long id;                  // 찜 ID
    private Long restaurantId;        // 식당 번호 (pno)
    private String restaurantName;    // 맛집 상호명
    private String category;          // 카테고리 (한식, 일식 등)
    private String guName;            // 지역구 (해운대구 등)
    private String mainMenu;          // 대표 메뉴
    private String address;           // 주소
    private String imageUrl;          // 대표 이미지
    private Double avgRating;         // 평균 별점
    private Integer reviewCount;      // 리뷰 개수
    private LocalDateTime regDate;    // 찜한 날짜
}
