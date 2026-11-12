package com.example.matgipv1.controller;

import com.example.matgipv1.service.GachaService;
import com.example.matgipv1.service.LogDataService;
import dto.GachaResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gacha")
@Log4j2
public class GachaApiController {

    private final GachaService gachaService;
    private final LogDataService logDataService;

    @GetMapping("/draw")
    public ResponseEntity<?> drawRestaurant(
            HttpServletRequest request,
            @RequestParam(required = false, defaultValue = "") String guName,
            @RequestParam(required = false, defaultValue = "") String category) {

        GachaResponseDTO result = gachaService.drawRandomRestaurant(guName, category);

        if (result == null) {
            return ResponseEntity.badRequest().body("해당 조건에 맞는 맛집이 없습니다. 지역 또는 카테고리를 변경해 보세요.");
        }

        // 활동 로그 기록
        try {
            logDataService.logActivity(
                    request,
                    "GACHA_DRAW",
                    "오늘 뭐 먹지? 가챠 뽑기: " + result.getRestaurantNameKo() + " (" + result.getGuName() + ")",
                    result.getPno(),
                    result.getRestaurantNameKo(),
                    guName + " / " + category,
                    "category=" + result.getCategory()
            );
        } catch (Exception e) {
            log.warn("가챠 로그 기록 오류 (무시 가능): {}", e.getMessage());
        }

        return ResponseEntity.ok(result);
    }
}
