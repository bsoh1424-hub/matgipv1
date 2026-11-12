package com.example.matgipv1.service;

import com.example.matgipv1.domain.Place;
import com.example.matgipv1.repository.PlaceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class OpenApiService {

    @Value("${openapi.serviceKey:}")
    private String serviceKey;

    private final PlaceRepository placeRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 100건 단위 단일 페이지 동기화 처리 (중복은 건너뛰고 신규만 저장)
    @Transactional
    public Map<String, Object> syncOpenData(int pageNo, int numOfRows) {
        Map<String, Object> result = new HashMap<>();
        int savedCount = 0;
        int skippedCount = 0;
        int totalCount = 0;

        if (serviceKey == null || serviceKey.trim().isEmpty()) {
            result.put("status", "NO_KEY");
            result.put("message", "공공데이터 서비스키(openapi.serviceKey)가 설정되어 있지 않습니다. application.properties를 확인해주세요.");
            result.put("savedCount", 0);
            result.put("skippedCount", 0);
            result.put("totalCount", 0);
            return result;
        }

        String rawKey = serviceKey.trim();
        String encodedKey;
        try {
            if (rawKey.contains("%")) {
                encodedKey = rawKey;
            } else {
                encodedKey = URLEncoder.encode(rawKey, StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            encodedKey = rawKey;
        }

        try {
            String urlStr = "https://apis.data.go.kr/6260000/FoodService/getFoodKr"
                    + "?serviceKey=" + encodedKey
                    + "&numOfRows=" + numOfRows
                    + "&pageNo=" + pageNo
                    + "&resultType=json";

            log.info("공공데이터 API 요청: pageNo={}, numOfRows={}", pageNo, numOfRows);

            URI uri = URI.create(urlStr);
            String responseStr = restTemplate.getForObject(uri, String.class);

            if (responseStr == null || responseStr.trim().isEmpty()) {
                result.put("status", "EMPTY_RESPONSE");
                result.put("message", "공공데이터 응답 데이터가 비어 있습니다.");
                result.put("savedCount", 0);
                result.put("skippedCount", 0);
                result.put("totalCount", 0);
                return result;
            }

            // 공공데이터 API 에러 응답(XML 형식으로 오는 경우 등) 체크
            if (responseStr.trim().startsWith("<")) {
                String errMsg = "공공데이터 서버 오류가 발생했습니다.";
                if (responseStr.contains("<errMsg>")) {
                    errMsg = extractXmlTag(responseStr, "errMsg");
                } else if (responseStr.contains("<returnAuthMsg>")) {
                    errMsg = extractXmlTag(responseStr, "returnAuthMsg");
                } else if (responseStr.contains("<cmmMsgHeader>")) {
                    errMsg = extractXmlTag(responseStr, "returnAuthMsg");
                }
                log.warn("공공데이터 API 오류 응답 수신: {}", responseStr);
                result.put("status", "API_ERROR");
                result.put("message", "공공데이터 포털 응답 오류: " + errMsg + " (서비스키나 사용 승인 여부를 확인해주세요)");
                result.put("savedCount", 0);
                result.put("skippedCount", 0);
                result.put("totalCount", 0);
                return result;
            }

            JsonNode rootNode = objectMapper.readTree(responseStr);

            // header 검증
            JsonNode headerNode = rootNode.path("getFoodKr").path("header");
            if (!headerNode.isMissingNode()) {
                String code = headerNode.path("code").asString("");
                String message = headerNode.path("message").asString("");
                if (!"00".equals(code) && !message.isEmpty()) {
                    result.put("status", "API_ERROR");
                    result.put("message", "공공데이터 오류 [" + code + "]: " + message);
                    result.put("savedCount", 0);
                    result.put("skippedCount", 0);
                    result.put("totalCount", 0);
                    return result;
                }
            }

            // 전체 데이터 건수 파싱
            JsonNode totalCountNode = rootNode.path("getFoodKr").path("totalCount");
            if (!totalCountNode.isMissingNode()) {
                try {
                    totalCount = totalCountNode.asInt(0);
                } catch (Exception ignored) {
                }
            }

            JsonNode itemsNode = rootNode.path("getFoodKr").path("item");

            if (!itemsNode.isArray() || itemsNode.isEmpty()) {
                result.put("status", "NO_ITEMS");
                result.put("message", "가져올 맛집 항목이 없습니다. (마지막 페이지이거나 데이터가 없습니다)");
                result.put("savedCount", 0);
                result.put("skippedCount", 0);
                result.put("totalCount", totalCount);
                result.put("fetchedCount", 0);
                return result;
            }

            for (JsonNode item : itemsNode) {
                String apiId = item.path("UC_SEQ").asString("");
                String name = item.path("MAIN_TITLE").asString("");
                String guName = item.path("GUGUN_NM").asString("");
                String address = item.path("ADDR1").asString("");
                String phone = item.path("CNTCT_TEL").asString("");
                String category = item.path("CATE_SUB_NAME").asString("");
                if (category.trim().isEmpty()) {
                    category = item.path("CATE_NM").asString("");
                }
                String mainMenu = item.path("RPRS_MENU").asString("");
                String operatingHours = item.path("USAGE_DAY_WEEK_AND_TIME").asString("");
                String summaryKo = item.path("ITEMCNTNTS").asString("");
                String imageUrl1 = item.path("MAIN_IMG_NORMAL").asString("");
                String imageUrl2 = item.path("MAIN_IMG_THUMB").asString("");

                if (imageUrl1.trim().isEmpty()) {
                    imageUrl1 = imageUrl2;
                }

                if (!name.trim().isEmpty() && !address.trim().isEmpty()) {
                    name = name.trim();
                    address = address.trim();

                    if (guName.trim().isEmpty()) {
                        guName = "부산시";
                    }

                    // 1. apiId로 기존 등록 여부 확인
                    boolean exists = false;
                    if (!apiId.trim().isEmpty()) {
                        Optional<Place> existingByApiId = placeRepository.findByApiId(apiId.trim());
                        if (existingByApiId.isPresent()) {
                            exists = true;
                        }
                    }

                    // 2. 상호명 + 상세주소 조합으로 기존 등록 여부 확인
                    if (!exists) {
                        exists = placeRepository.existsByRestaurantNameKoAndAddress(name, address);
                    }

                    // 중복되는 내용은 수정하지 않고 그대로 보존(건너뜀)
                    if (exists) {
                        skippedCount++;
                    } else {
                        Place place = Place.builder()
                                .restaurantNameKo(name)
                                .guName(guName)
                                .address(address)
                                .phone(phone)
                                .category(category)
                                .mainMenu(mainMenu)
                                .operatingHours(operatingHours)
                                .summaryKo(summaryKo)
                                .imageUrl1(imageUrl1)
                                .imageUrl2(imageUrl2)
                                .sourceType("API")
                                .apiId(apiId)
                                .build();

                        placeRepository.save(place);
                        savedCount++;
                    }
                }
            }

            log.info("공공데이터 [페이지 {}] 완료: 신규 추가 {}건, 중복 건너뜀 {}건 (조회 {}건)", pageNo, savedCount, skippedCount, itemsNode.size());

            result.put("status", "SUCCESS");
            result.put("message", "공공데이터 동기화 완료!");
            result.put("savedCount", savedCount);
            result.put("skippedCount", skippedCount);
            result.put("fetchedCount", itemsNode.size());
            result.put("totalCount", totalCount);

        } catch (Exception e) {
            log.error("공공데이터 동기화 중 오류 발생 (page={})", pageNo, e);
            result.put("status", "ERROR");
            result.put("message", "오류 발생: " + e.getMessage());
            result.put("savedCount", savedCount);
            result.put("skippedCount", skippedCount);
            result.put("totalCount", totalCount);
        }

        return result;
    }

    // 100개씩 전체 페이지를 순회하여 모든 공공데이터를 동기화 (기존 데이터 수정 없이 신규만 누적 추가)
    public Map<String, Object> syncAllOpenData() {
        Map<String, Object> finalResult = new HashMap<>();
        int totalSaved = 0;
        int totalSkipped = 0;
        int totalProcessed = 0;
        int pageNo = 1;
        int numOfRows = 100;
        int totalApiCount = 0;

        log.info("=== 공공데이터 전체 동기화 시작 (100건씩 전체 순회) ===");

        while (true) {
            Map<String, Object> pageResult = syncOpenData(pageNo, numOfRows);
            String status = (String) pageResult.get("status");

            if ("SUCCESS".equals(status)) {
                int saved = (int) pageResult.getOrDefault("savedCount", 0);
                int skipped = (int) pageResult.getOrDefault("skippedCount", 0);
                int fetched = (int) pageResult.getOrDefault("fetchedCount", 0);
                totalApiCount = (int) pageResult.getOrDefault("totalCount", 0);

                totalSaved += saved;
                totalSkipped += skipped;
                totalProcessed += fetched;

                // 가져온 건수가 없거나 요청한 건수(100건)보다 적으면 마지막 페이지로 판단하여 종료
                if (fetched < numOfRows) {
                    break;
                }

                // totalCount에 이미 도달했으면 종료
                if (totalApiCount > 0 && totalProcessed >= totalApiCount) {
                    break;
                }

                pageNo++;

                // 공공데이터 API 서버 부하 방지를 위한 미세 대기 (100ms)
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }

            } else if ("NO_ITEMS".equals(status)) {
                // 더 이상 데이터가 없으므로 종료
                break;
            } else {
                // 에러 발생 시 진행 중단 및 에러 반환
                finalResult.put("status", status);
                finalResult.put("message", pageResult.get("message"));
                finalResult.put("savedCount", totalSaved);
                finalResult.put("skippedCount", totalSkipped);
                finalResult.put("totalProcessed", totalProcessed);
                finalResult.put("lastPage", pageNo);
                return finalResult;
            }
        }

        log.info("=== 공공데이터 전체 동기화 완료: 총 조회 {}건, 신규 저장 {}건, 기존 중복 유지 {}건 ===", totalProcessed, totalSaved, totalSkipped);

        finalResult.put("status", "SUCCESS");
        finalResult.put("message", "공공데이터 전체 동기화가 성공적으로 완료되었습니다!");
        finalResult.put("savedCount", totalSaved);
        finalResult.put("skippedCount", totalSkipped);
        finalResult.put("totalProcessed", totalProcessed);
        finalResult.put("totalPages", pageNo);
        finalResult.put("totalApiCount", totalApiCount);

        return finalResult;
    }

    private String extractXmlTag(String xml, String tagName) {
        try {
            String openTag = "<" + tagName + ">";
            String closeTag = "</" + tagName + ">";
            int start = xml.indexOf(openTag);
            int end = xml.indexOf(closeTag);
            if (start != -1 && end != -1 && end > start) {
                return xml.substring(start + openTag.length(), end).trim();
            }
        } catch (Exception ignored) {
        }
        return xml.length() > 100 ? xml.substring(0, 100) + "..." : xml;
    }

    // 기존 전체 수집용 메서드 유지
    @Transactional
    public int fetchAndSaveOpenData() {
        Map<String, Object> res = syncAllOpenData();
        return (int) res.getOrDefault("savedCount", 0);
    }
}
