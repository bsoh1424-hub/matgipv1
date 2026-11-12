package com.example.matgipv1.service;

import com.example.matgipv1.domain.Place;
import dto.PageRequestDTO;
import dto.PageResponseDTO;
import dto.PlaceDTO;

import java.util.List;

public interface PlaceService {
    // C: 맛집 등록
    Long register(PlaceDTO placeDTO);

    // R: 맛집 상세 조회
    PlaceDTO readOne(Long pno);

    // U: 맛집 정보 수정
    void modify(PlaceDTO placeDTO);

    // D: 맛집 삭제
    void remove(Long pno);

    // R: 목록 및 검색/페이징 처리 조회
    // [수정] 목록에 필요한 평점·북마크 정보까지 서비스에서 조합합니다.
    PageResponseDTO<PlaceDTO> getList(PageRequestDTO pageRequestDTO, Long memberNo);

    // 전체 맛집 목록 조회 (지도 표시용)
    List<PlaceDTO> getAllPlaces();

    // DTO -> Entity
    default Place dtoToEntity(PlaceDTO dto) {
        return Place.builder()
                .pno(dto.getPno())
                .guName(dto.getGuName())
                .restaurantNameKo(dto.getRestaurantNameKo())
                .restaurantNameEn(dto.getRestaurantNameEn())
                .category(dto.getCategory())
                .summaryKo(dto.getSummaryKo())
                .summaryEn(dto.getSummaryEn())
                .imageUrl1(dto.getImageUrl1())
                .imageUrl2(dto.getImageUrl2())
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .operatingHours(dto.getOperatingHours())
                .breakTime(dto.getBreakTime())
                .offDays(dto.getOffDays())
                .mainMenu(dto.getMainMenu())
                .priceRange(dto.getPriceRange())
                .parking(dto.getParking())
                .pageNum(dto.getPageNum())
                .build();
    }

    // Entity -> DTO
    default PlaceDTO entityToDto(Place place) {
        return PlaceDTO.builder()
                .pno(place.getPno())
                .guName(place.getGuName())
                .restaurantNameKo(place.getRestaurantNameKo())
                .restaurantNameEn(place.getRestaurantNameEn())
                .category(place.getCategory())
                .summaryKo(place.getSummaryKo())
                .summaryEn(place.getSummaryEn())
                .imageUrl1(place.getImageUrl1())
                .imageUrl2(place.getImageUrl2())
                .address(place.getAddress())
                .phone(place.getPhone())
                .operatingHours(place.getOperatingHours())
                .breakTime(place.getBreakTime())
                .offDays(place.getOffDays())
                .mainMenu(place.getMainMenu())
                .priceRange(place.getPriceRange())
                .parking(place.getParking())
                .pageNum(place.getPageNum())
                .regDate(place.getRegDate())
                .modDate(place.getModDate())
                .build();
    }
}
