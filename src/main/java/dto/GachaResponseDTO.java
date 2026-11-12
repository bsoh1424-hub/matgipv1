package dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GachaResponseDTO {
    private Long id;                // JS 호환용 (pno)
    private Long pno;               // 맛집 PK
    private String guName;          // 지역구
    private String restaurantNameKo;// 맛집 상호명 (busan_restaurants 테이블)
    private String restaurantNameEn;// 영문 상호명
    private String category;        // 카테고리 (한식, 일식, 중식, 양식 등)
    private String summaryKo;       // 요약 소개글
    private String mainMenu;        // 대표 메뉴
    private String imageUrl1;       // 실제 DB 사진 URL 1
    private String imageUrl2;       // 실제 DB 사진 URL 2
    private String address;         // 상세 주소
    private String phone;           // 전화번호
}
