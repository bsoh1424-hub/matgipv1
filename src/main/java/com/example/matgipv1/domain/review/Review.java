package com.example.matgipv1.domain.review;

import com.example.matgipv1.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "review",
        uniqueConstraints = {@UniqueConstraint(name = "uk_review_restaurant_member", columnNames = {"restaurant_id", "member_id"})},
        indexes = {@Index(name = "idx_review_restaurant_created", columnList = "restaurant_id, created_at")}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Integer rating;

    @Enumerated(EnumType.STRING)
    @Column(name = "solo_level")
    private SoloLevel soloLevel;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Boolean hidden;

    @Column(name = "original_image_name")
    private String originalImageName;

    @Column(name = "store_image_name")
    private String storeImageName;

    @Column(name = "thumbnail_name")
    private String thumbnailName;

    // 리뷰 내용 수정
    public void change(Integer rating, SoloLevel soloLevel, String content) {
        if (rating != null) this.rating = rating;
        if (soloLevel != null) this.soloLevel = soloLevel;
        if (content != null) this.content = content;
    }

    // 사진 정보 수정
    public void updateImage(String originalImageName, String storeImageName, String thumbnailName) {
        this.originalImageName = originalImageName;
        this.storeImageName = storeImageName;
        this.thumbnailName = thumbnailName;
    }

    // 사진 삭제
    public void clearImage() {
        this.originalImageName = null;
        this.storeImageName = null;
        this.thumbnailName = null;
    }

    public void changeHidden(Boolean hidden) {
        this.hidden = hidden;
    }
}
