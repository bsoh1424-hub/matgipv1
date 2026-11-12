package com.example.matgipv1.service;

import com.example.matgipv1.domain.Place;
import com.example.matgipv1.repository.PlaceRepository;
import dto.GachaResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Log4j2
public class GachaService {

    private final PlaceRepository placeRepository;
    private final Random random = new Random();

    public GachaResponseDTO drawRandomRestaurant(String guName, String category) {
        log.info(">>> [가챠 요청 수신] guName: '{}', category: '{}'", guName, category);

        // 1. 파라미터 정제 ("부산진구(서면)" -> "부산진구", "전체 지역..." -> null)
        String cleanGu = cleanParam(guName);
        String cleanCategory = cleanParam(category);

        if ("카페/디저트".equals(cleanCategory) || "카페/베이커리".equals(cleanCategory)) {
            cleanCategory = "카페";
        }

        log.info(">>> [정제된 SQL 파라미터] guName: '{}', category: '{}'", cleanGu, cleanCategory);

        // 2. busan_restaurants (Place) 테이블에서 조건에 맞는 맛집 목록 조회
        List<Place> list = placeRepository.findForGacha(cleanGu, cleanCategory);

        // 만약 카테고리가 매칭되지 않아 비어있는 경우, 지역 조건만으로 fallback 재조회
        if (list.isEmpty() && StringUtils.hasText(cleanCategory)) {
            log.info(">>> 카테고리 매칭 결과 없음 -> 지역구({}) 기준으로 재조회 시도", cleanGu);
            list = placeRepository.findForGacha(cleanGu, null);
        }

        // 전체 fallback
        if (list.isEmpty()) {
            log.info(">>> 전체 맛집 중에서 랜덤 선택 시도");
            list = placeRepository.findAll();
        }

        if (list.isEmpty()) {
            return null;
        }

        // 3. SQL로 조회한 실제 식당 리스트에서 무작위 1개 선정
        Place selected = list.get(random.nextInt(list.size()));
        log.info(">>> [가챠 당첨 맛집] pno: {}, restaurantNameKo: '{}', imageUrl1: '{}'",
                selected.getPno(), selected.getRestaurantNameKo(), selected.getImageUrl1());

        // 대표 이미지 URL 결정 (imageUrl1 우선, 없으면 imageUrl2)
        String finalImageUrl = StringUtils.hasText(selected.getImageUrl1()) ? selected.getImageUrl1() : selected.getImageUrl2();

        return GachaResponseDTO.builder()
                .id(selected.getPno())
                .pno(selected.getPno())
                .guName(selected.getGuName())
                .restaurantNameKo(selected.getRestaurantNameKo())
                .restaurantNameEn(selected.getRestaurantNameEn())
                .category(selected.getCategory())
                .summaryKo(selected.getSummaryKo())
                .mainMenu(selected.getMainMenu())
                .imageUrl1(finalImageUrl)
                .imageUrl2(selected.getImageUrl2())
                .address(selected.getAddress())
                .phone(selected.getPhone())
                .build();
    }

    private String cleanParam(String param) {
        if (!StringUtils.hasText(param) || param.contains("전체") || "null".equals(param) || "undefined".equals(param)) {
            return null;
        }
        if (param.contains("(")) {
            param = param.substring(0, param.indexOf("(")).trim();
        }
        return param.trim();
    }
}
