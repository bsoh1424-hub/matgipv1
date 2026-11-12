package com.example.matgipv1.service;

import com.example.matgipv1.domain.Member;
import com.example.matgipv1.domain.Place;
import com.example.matgipv1.domain.review.Review;
import com.example.matgipv1.domain.review.SoloLevel;
import com.example.matgipv1.repository.MemberRepository;
import com.example.matgipv1.repository.PlaceRepository;
import com.example.matgipv1.repository.ReviewRepository;
import dto.ReviewDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final PlaceRepository placeRepository;

    @Value("${upload.path:C:/upload/matgipv1}")
    private String uploadPath;

    // 해당 맛집의 전체 리뷰 목록 조회 (최신순)
    @Transactional(readOnly = true)
    public List<ReviewDTO> getReviewsByRestaurant(Long restaurantId) {
        // [수정] 상세 페이지에서는 관리자 숨김 리뷰를 노출하지 않습니다.
        List<Review> reviews = reviewRepository.findVisibleByRestaurantIdOrderByRegDateDesc(restaurantId);
        return convertToDTOList(reviews);
    }

    // 해당 맛집의 최신 리뷰 3개 조회
    @Transactional(readOnly = true)
    public List<ReviewDTO> getTop3ReviewsByRestaurant(Long restaurantId) {
        // [수정] 최신 3건도 숨김 처리된 리뷰를 제외합니다.
        List<Review> top3 = reviewRepository.findVisibleReviewsByRestaurantIdOrderByRegDateDesc(restaurantId)
                .stream().limit(3).toList();
        return convertToDTOList(top3);
    }

    // 마이페이지: 특정 회원이 작성한 전체 리뷰 목록 조회 (맛집 상호명 매핑)
    @Transactional(readOnly = true)
    public List<ReviewDTO> getReviewsByMember(Long memberId) {
        List<Review> reviews = reviewRepository.findByMemberIdOrderByRegDateDesc(memberId);
        if (reviews.isEmpty()) {
            return List.of();
        }

        return convertToDTOListWithRestaurant(reviews);
    }

    // 관리자: 시스템 전체 회원의 리뷰 목록 조회 (최신순, 맛집 상호명 및 작성자 정보 포함)
    @Transactional(readOnly = true)
    public List<ReviewDTO> getAllReviewsForAdmin() {
        List<Review> reviews = reviewRepository.findAllByOrderByRegDateDesc();
        return convertToDTOListWithRestaurant(reviews);
    }

    // 관리자: 리뷰 숨김(블라인드) 상태 변경
    public boolean changeHiddenStatus(Long reviewId, boolean hidden) {
        Review review = reviewRepository.findById(reviewId).orElse(null);
        if (review == null) return false;

        review.changeHidden(hidden);
        log.info("리뷰 숨김 상태 변경 - reviewId: {}, hidden: {}", reviewId, hidden);
        return true;
    }

    // 리뷰 신규 등록 (사진 업로드 포함)
    public Long addReview(Long restaurantId, Long memberId, Integer rating, SoloLevel soloLevel, String content, MultipartFile file) {
        if (rating == null || rating < 1) rating = 5;
        if (rating > 5) rating = 5;

        String originalName = null;
        String storeName = null;

        // 파일 업로드 처리
        if (file != null && !file.isEmpty()) {
            originalName = file.getOriginalFilename();
            String extension = "";
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }
            storeName = UUID.randomUUID().toString() + extension;

            try {
                File dir = new File(uploadPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                Path savePath = Paths.get(uploadPath, storeName);
                file.transferTo(savePath.toFile());
                log.info(">>> [리뷰 사진 저장 완료] 경로: {}", savePath.toAbsolutePath());
            } catch (IOException e) {
                log.error(">>> [리뷰 사진 저장 실패]: {}", e.getMessage(), e);
                storeName = null;
            }
        }

        Review review = Review.builder()
                .restaurantId(restaurantId)
                .memberId(memberId)
                .rating(rating)
                .soloLevel(soloLevel != null ? soloLevel : SoloLevel.LV1)
                .content(content != null ? content.trim() : "")
                .hidden(false)
                .originalImageName(originalName)
                .storeImageName(storeName)
                .thumbnailName(storeName)
                .build();

        Review saved = reviewRepository.save(review);
        log.info("리뷰 등록 완료 - reviewId: {}, restaurantId: {}, memberId: {}, storeImage: {}",
                saved.getId(), restaurantId, memberId, storeName);
        return saved.getId();
    }

    // 리뷰 수정 (작성자 본인 또는 관리자)
    public boolean modifyReview(Long reviewId, Long memberId, boolean isAdmin,
                                Integer rating, SoloLevel soloLevel, String content,
                                MultipartFile file, boolean deleteImage) {
        Review review = reviewRepository.findById(reviewId).orElse(null);
        if (review == null) {
            return false;
        }

        if (!isAdmin && (memberId == null || !review.getMemberId().equals(memberId))) {
            log.warn("리뷰 수정 권한 없음 - reviewId: {}, memberId: {}", reviewId, memberId);
            return false;
        }

        if (rating == null || rating < 1) rating = 5;
        if (rating > 5) rating = 5;

        review.change(rating, soloLevel, content != null ? content.trim() : "");

        // 1. 기존 사진 삭제 요청이 있는 경우
        if (deleteImage && StringUtils.hasText(review.getStoreImageName())) {
            deletePhysicalFile(review.getStoreImageName());
            review.clearImage();
        }

        // 2. 새 사진 파일이 업로드된 경우 교체
        if (file != null && !file.isEmpty()) {
            if (StringUtils.hasText(review.getStoreImageName())) {
                deletePhysicalFile(review.getStoreImageName());
            }

            String originalName = file.getOriginalFilename();
            String extension = "";
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }
            String storeName = UUID.randomUUID().toString() + extension;

            try {
                File dir = new File(uploadPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                Path savePath = Paths.get(uploadPath, storeName);
                file.transferTo(savePath.toFile());
                review.updateImage(originalName, storeName, storeName);
                log.info(">>> [리뷰 사진 수정/교체 완료] 파일: {}", storeName);
            } catch (IOException e) {
                log.error(">>> [리뷰 사진 저장 실패]: {}", e.getMessage(), e);
            }
        }

        log.info("리뷰 수정 완료 - reviewId: {}", reviewId);
        return true;
    }

    // 리뷰 삭제 (작성자 본인 또는 관리자만 가능, 파일도 함께 삭제)
    public boolean deleteReview(Long reviewId, Long memberId, boolean isAdmin) {
        Review review = reviewRepository.findById(reviewId).orElse(null);
        if (review == null) {
            return false;
        }

        if (isAdmin || (memberId != null && review.getMemberId().equals(memberId))) {
            // 업로드된 파일이 있으면 물리적 삭제
            if (StringUtils.hasText(review.getStoreImageName())) {
                deletePhysicalFile(review.getStoreImageName());
            }

            reviewRepository.deleteById(reviewId);
            log.info("리뷰 삭제 완료 - reviewId: {}", reviewId);
            return true;
        }

        log.warn("리뷰 삭제 권한 없음 - reviewId: {}, memberId: {}", reviewId, memberId);
        return false;
    }

    private void deletePhysicalFile(String fileName) {
        try {
            Path filePath = Paths.get(uploadPath, fileName);
            Files.deleteIfExists(filePath);
            log.info(">>> [물리적 사진 파일 삭제 완료] 파일: {}", fileName);
        } catch (Exception e) {
            log.warn(">>> [물리적 사진 파일 삭제 오류 (무시 가능)]: {}", e.getMessage());
        }
    }

    // 엔티티 -> DTO 변환 헬퍼 (식당명 + 작성자 닉네임 일괄 매핑)
    private List<ReviewDTO> convertToDTOListWithRestaurant(List<Review> reviews) {
        if (reviews.isEmpty()) return List.of();

        List<Long> pnos = reviews.stream().map(Review::getRestaurantId).distinct().collect(Collectors.toList());
        Map<Long, String> placeNameMap = placeRepository.findAllById(pnos).stream()
                .collect(Collectors.toMap(Place::getPno, Place::getRestaurantNameKo, (n1, n2) -> n1));

        List<Long> memberIds = reviews.stream().map(Review::getMemberId).distinct().collect(Collectors.toList());
        Map<Long, String> memberNickMap = memberRepository.findAllById(memberIds).stream()
                .collect(Collectors.toMap(Member::getMno, Member::getNickname, (n1, n2) -> n1));

        return reviews.stream().map(r -> {
            String rName = placeNameMap.getOrDefault(r.getRestaurantId(), "등록된 맛집");
            String nickname = memberNickMap.getOrDefault(r.getMemberId(), "익명회원");
            String imageUrl = StringUtils.hasText(r.getStoreImageName()) ? "/upload/" + r.getStoreImageName() : null;

            return ReviewDTO.builder()
                    .id(r.getId())
                    .restaurantId(r.getRestaurantId())
                    .restaurantName(rName)
                    .memberId(r.getMemberId())
                    .writerNickname(nickname)
                    .rating(r.getRating() != null ? r.getRating() : 5)
                    .soloLevel(r.getSoloLevel())
                    .content(r.getContent())
                    .hidden(r.getHidden())
                    .originalImageName(r.getOriginalImageName())
                    .storeImageName(r.getStoreImageName())
                    .thumbnailName(r.getThumbnailName())
                    .imageUrl(imageUrl)
                    .regDate(r.getRegDate())
                    .build();
        }).collect(Collectors.toList());
    }

    private List<ReviewDTO> convertToDTOList(List<Review> reviews) {
        if (reviews.isEmpty()) return List.of();

        List<Long> memberIds = reviews.stream()
                .map(Review::getMemberId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, String> memberNickMap = memberRepository.findAllById(memberIds).stream()
                .collect(Collectors.toMap(Member::getMno, Member::getNickname, (n1, n2) -> n1));

        return reviews.stream().map(r -> {
            String nickname = memberNickMap.getOrDefault(r.getMemberId(), "익명회원");
            String imageUrl = StringUtils.hasText(r.getStoreImageName()) ? "/upload/" + r.getStoreImageName() : null;

            return ReviewDTO.builder()
                    .id(r.getId())
                    .restaurantId(r.getRestaurantId())
                    .memberId(r.getMemberId())
                    .writerNickname(nickname)
                    .rating(r.getRating() != null ? r.getRating() : 5)
                    .soloLevel(r.getSoloLevel())
                    .content(r.getContent())
                    .hidden(r.getHidden())
                    .originalImageName(r.getOriginalImageName())
                    .storeImageName(r.getStoreImageName())
                    .thumbnailName(r.getThumbnailName())
                    .imageUrl(imageUrl)
                    .regDate(r.getRegDate())
                    .build();
        }).collect(Collectors.toList());
    }
}
