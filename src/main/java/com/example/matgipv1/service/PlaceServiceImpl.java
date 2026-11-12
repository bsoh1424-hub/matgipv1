package com.example.matgipv1.service;

import com.example.matgipv1.domain.Place;
import com.example.matgipv1.repository.PlaceRepository;
import com.example.matgipv1.repository.ReviewRepository;
import dto.PageRequestDTO;
import dto.PageResponseDTO;
import dto.PlaceDTO;
import org.modelmapper.ModelMapper;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class PlaceServiceImpl implements PlaceService {
    private final PlaceRepository placeRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewService reviewService;
    private final BookmarkService bookmarkService;
    private final ModelMapper modelMapper;

    @Override
    public Long register(PlaceDTO placeDTO) {
        Place place = dtoToEntity(placeDTO);
        Long pno = placeRepository.save(place).getPno();
        return pno;
    }

    @Override
    @Transactional(readOnly = true)
    public PlaceDTO readOne(Long pno) {
        Optional<Place> result = placeRepository.findById(pno);
        Place place = result.orElseThrow(() -> new IllegalArgumentException("해당 맛집이 존재하지 않습니다. pno=" + pno));
        return entityToDto(place);
    }

    @Override
    public void modify(PlaceDTO placeDTO) {
        Optional<Place> result = placeRepository.findById(placeDTO.getPno());
        Place place = result.orElseThrow(() -> new IllegalArgumentException("해당 맛집이 존재하지 않습니다. pno=" + placeDTO.getPno()));

        place.change(
                placeDTO.getGuName(),
                placeDTO.getRestaurantNameKo(),
                placeDTO.getRestaurantNameEn(),
                placeDTO.getCategory(),
                placeDTO.getSummaryKo(),
                placeDTO.getSummaryEn(),
                placeDTO.getImageUrl1(),
                placeDTO.getImageUrl2(),
                placeDTO.getAddress(),
                placeDTO.getPhone(),
                placeDTO.getOperatingHours(),
                placeDTO.getBreakTime(),
                placeDTO.getOffDays(),
                placeDTO.getMainMenu(),
                placeDTO.getPriceRange(),
                placeDTO.getParking(),
                placeDTO.getPageNum()
        );

        placeRepository.save(place);
    }

    @Override
    public void remove(Long pno) {
        if (pno == null) return;
        try {
            // 맛집 삭제 시 연관된 리뷰 데이터도 함께 삭제
            reviewRepository.deleteByRestaurantId(pno);
        } catch (Exception e) {
            log.warn("연관 리뷰 삭제 중 오류 (무시 가능): {}", e.getMessage());
        }
        placeRepository.deleteById(pno);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<PlaceDTO> getList(PageRequestDTO pageRequestDTO, Long memberNo) {
        Pageable pageable = pageRequestDTO.getPageable("pno");

        Page<Place> result = placeRepository.searchList(
                pageRequestDTO.getGuName(),
                pageRequestDTO.getCategory(),
                pageRequestDTO.getKeyword(),
                pageable
        );

        List<PlaceDTO> dtoList = result.getContent().stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());

        // [수정] 목록 카드용 평균 평점과 북마크 정보를 서비스에서 조합합니다.
        dtoList.forEach(place -> {
            List<dto.ReviewDTO> reviews = reviewService.getReviewsByRestaurant(place.getPno());
            double avgRating = reviews.isEmpty() ? 0.0 : Math.round(
                    reviews.stream().mapToInt(dto.ReviewDTO::getRating).average().orElse(0.0) * 10.0
            ) / 10.0;
            place.setAvgRating(avgRating);
            place.setReviewCount(reviews.size());
            place.setBookmarkCount(bookmarkService.getBookmarkCount(place.getPno()));
            place.setIsBookmarked(memberNo != null && bookmarkService.isBookmarked(memberNo, place.getPno()));
        });

        return PageResponseDTO.<PlaceDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) result.getTotalElements())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlaceDTO> getAllPlaces() {
        return placeRepository.findAll().stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }
}
