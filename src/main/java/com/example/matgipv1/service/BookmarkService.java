package com.example.matgipv1.service;

import com.example.matgipv1.domain.Bookmark;
import com.example.matgipv1.domain.Place;
import com.example.matgipv1.domain.review.Review;
import com.example.matgipv1.repository.BookmarkRepository;
import com.example.matgipv1.repository.PlaceRepository;
import com.example.matgipv1.repository.ReviewRepository;
import dto.BookmarkDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final PlaceRepository placeRepository;
    private final ReviewRepository reviewRepository;

    // 1. 찜(북마크) 토글 - 찜이 되어 있으면 해제, 안되어 있으면 등록
    public boolean toggleBookmark(Long memberId, Long restaurantId) {
        if (memberId == null || restaurantId == null) return false;

        Optional<Bookmark> bookmarkOpt = bookmarkRepository.findByMemberIdAndRestaurantId(memberId, restaurantId);
        if (bookmarkOpt.isPresent()) {
            bookmarkRepository.delete(bookmarkOpt.get());
            log.info(">>> [찜 해제] memberId: {}, restaurantId: {}", memberId, restaurantId);
            return false; // 해제됨
        } else {
            Bookmark bookmark = Bookmark.builder()
                    .memberId(memberId)
                    .restaurantId(restaurantId)
                    .build();
            bookmarkRepository.save(bookmark);
            log.info(">>> [찜 등록] memberId: {}, restaurantId: {}", memberId, restaurantId);
            return true; // 등록됨
        }
    }

    // 2. 찜 여부 확인
    @Transactional(readOnly = true)
    public boolean isBookmarked(Long memberId, Long restaurantId) {
        if (memberId == null || restaurantId == null) return false;
        return bookmarkRepository.existsByMemberIdAndRestaurantId(memberId, restaurantId);
    }

    // 3. 식당별 총 찜 개수
    @Transactional(readOnly = true)
    public long getBookmarkCount(Long restaurantId) {
        if (restaurantId == null) return 0;
        return bookmarkRepository.countByRestaurantId(restaurantId);
    }

    // 4. 찜 단건 삭제 (마이페이지용)
    public void removeBookmark(Long memberId, Long restaurantId) {
        if (memberId == null || restaurantId == null) return;
        bookmarkRepository.deleteByMemberIdAndRestaurantId(memberId, restaurantId);
        log.info(">>> [마이페이지 찜 삭제] memberId: {}, restaurantId: {}", memberId, restaurantId);
    }

    // 5. 마이페이지: 특정 회원의 찜한 맛집 목록 조회 (맛집 상세 정보 + 평균 별점 + 리뷰 수)
    @Transactional(readOnly = true)
    public List<BookmarkDTO> getMyBookmarks(Long memberId) {
        if (memberId == null) return List.of();

        List<Bookmark> bookmarks = bookmarkRepository.findByMemberIdOrderByRegDateDesc(memberId);
        if (bookmarks.isEmpty()) return List.of();

        List<Long> pnos = bookmarks.stream().map(Bookmark::getRestaurantId).distinct().collect(Collectors.toList());
        Map<Long, Place> placeMap = placeRepository.findAllById(pnos).stream()
                .collect(Collectors.toMap(Place::getPno, p -> p, (p1, p2) -> p1));

        return bookmarks.stream().map(b -> {
            Place place = placeMap.get(b.getRestaurantId());
            if (place == null) return null;

            // 해당 식당의 리뷰들로 평균 별점 및 리뷰 수 계산
            List<Review> reviews = reviewRepository.findByRestaurantIdOrderByRegDateDesc(place.getPno());
            double avgRating = 0.0;
            if (!reviews.isEmpty()) {
                double sum = reviews.stream().mapToInt(Review::getRating).sum();
                avgRating = Math.round((sum / reviews.size()) * 10.0) / 10.0;
            }

            String imgUrl = (place.getImageUrl1() != null && !place.getImageUrl1().isBlank())
                    ? place.getImageUrl1()
                    : place.getImageUrl2();

            return BookmarkDTO.builder()
                    .id(b.getId())
                    .restaurantId(place.getPno())
                    .restaurantName(place.getRestaurantNameKo())
                    .category(place.getCategory())
                    .guName(place.getGuName())
                    .mainMenu(place.getMainMenu())
                    .address(place.getAddress())
                    .imageUrl(imgUrl)
                    .avgRating(avgRating)
                    .reviewCount(reviews.size())
                    .regDate(b.getRegDate())
                    .build();
        }).filter(java.util.Objects::nonNull).collect(Collectors.toList());
    }
}
