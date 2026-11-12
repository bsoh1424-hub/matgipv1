package dto;

import com.example.matgipv1.domain.review.SoloLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {
    private Long id;
    private Long restaurantId;
    private String restaurantName;    // 맛집 상호명 (마이페이지/관리자 연동용)
    private Long memberId;
    private String writerNickname;
    private Integer rating;
    private SoloLevel soloLevel;
    private String content;
    private Boolean hidden;           // 숨김(블라인드) 여부
    private LocalDateTime regDate;

    // 사진 관련 필드
    private String originalImageName;
    private String storeImageName;
    private String thumbnailName;
    private String imageUrl;          // 브라우저 렌더링용 이미지 경로 (/upload/...)
}
