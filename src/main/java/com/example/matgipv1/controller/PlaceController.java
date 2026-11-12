package com.example.matgipv1.controller;

import com.example.matgipv1.domain.review.SoloLevel;
import com.example.matgipv1.service.BookmarkService;
import com.example.matgipv1.service.PlaceService;
import com.example.matgipv1.service.ReviewService;
import dto.PageRequestDTO;
import dto.PageResponseDTO;
import dto.PlaceDTO;
import dto.ReviewDTO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/place")
@Log4j2
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;
    private final ReviewService reviewService;
    private final BookmarkService bookmarkService;

    @Value("${upload.path:C:/upload/matgipv1}")
    private String uploadPath;

    // 1. 목록 페이지 조회
    @GetMapping("/list")
    public void list(PageRequestDTO pageRequestDTO, HttpSession session, Model model) {
        log.info("Place List GET... guName: " + pageRequestDTO.getGuName());
        Long memberNo = (Long) session.getAttribute("loginMember");
        // [수정] 목록 부가 정보 조합은 PlaceService에서 처리하고 컨트롤러는 모델만 전달합니다.
        PageResponseDTO<PlaceDTO> responseDTO = placeService.getList(pageRequestDTO, memberNo);
        model.addAttribute("responseDTO", responseDTO);
        model.addAttribute("pageRequestDTO", pageRequestDTO);
    }

    // 2. 관리자 전용 신규 맛집 등록 페이지 이동
    @GetMapping("/register")
    public String registerGET(HttpSession session, RedirectAttributes redirectAttributes) {
        String role = (String) session.getAttribute("loginRole");
        if (!"ROLE_ADMIN".equals(role)) {
            redirectAttributes.addFlashAttribute("error", "맛집 등록 권한이 없습니다. (관리자 전용 기능)");
            return "redirect:/place/list";
        }
        log.info("PlaceController registerGET() 관리자 접속");
        return "place/register";
    }

    // 3. 신규 맛집 등록 처리 (대표 이미지 2개 파일 업로드 & DB insert 매칭)
    @PostMapping("/register")
    public String registerPOST(
            PlaceDTO placeDTO,
            @RequestParam(value = "image1File", required = false) MultipartFile image1File,
            @RequestParam(value = "image2File", required = false) MultipartFile image2File,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        String role = (String) session.getAttribute("loginRole");
        if (!"ROLE_ADMIN".equals(role)) {
            redirectAttributes.addFlashAttribute("error", "관리자만 맛집을 등록할 수 있습니다.");
            return "redirect:/place/list";
        }

        log.info("PlaceController registerPOST() - 상호명: {}", placeDTO.getRestaurantNameKo());

        // 대표 이미지 1 파일 업로드 처리
        if (image1File != null && !image1File.isEmpty()) {
            String uploadedUrl1 = saveUploadedImage(image1File);
            if (uploadedUrl1 != null) {
                placeDTO.setImageUrl1(uploadedUrl1);
            }
        }

        // 서브 이미지 2 파일 업로드 처리
        if (image2File != null && !image2File.isEmpty()) {
            String uploadedUrl2 = saveUploadedImage(image2File);
            if (uploadedUrl2 != null) {
                placeDTO.setImageUrl2(uploadedUrl2);
            }
        }

        Long pno = placeService.register(placeDTO);
        log.info(">>> 신규 맛집 등록 완료 - PNO: {}, 상호명: {}, 이미지1: {}, 이미지2: {}",
                pno, placeDTO.getRestaurantNameKo(), placeDTO.getImageUrl1(), placeDTO.getImageUrl2());

        redirectAttributes.addFlashAttribute("msg", "신규 맛집 [" + placeDTO.getRestaurantNameKo() + "]이(가) 성공적으로 등록되었습니다.");
        return "redirect:/place/read?pno=" + pno;
    }

    // 4. 상세 조회 및 수정 페이지 이동
    @GetMapping({"/read", "/modify"})
    public void read(Long pno, PageRequestDTO pageRequestDTO, HttpSession session, Model model) {
        log.info("PlaceController read() 호출 - pno: " + pno);

        PlaceDTO placeDTO = placeService.readOne(pno);

        // 해당 맛집의 전체 리뷰 목록 및 최신 3개 리뷰 조회
        List<ReviewDTO> reviews = reviewService.getReviewsByRestaurant(pno);
        List<ReviewDTO> topReviews = reviewService.getTop3ReviewsByRestaurant(pno);

        // 평균 별점 계산
        double avgRating = 0.0;
        if (!reviews.isEmpty()) {
            double sum = reviews.stream().mapToInt(ReviewDTO::getRating).sum();
            avgRating = Math.round((sum / reviews.size()) * 10.0) / 10.0;
        }

        placeDTO.setAvgRating(avgRating);
        placeDTO.setReviewCount(reviews.size());

        // 찜(북마크) 수 및 현재 로그인 사용자의 찜 여부
        Long memberNo = (Long) session.getAttribute("loginMember");
        placeDTO.setBookmarkCount(bookmarkService.getBookmarkCount(pno));
        placeDTO.setIsBookmarked(memberNo != null && bookmarkService.isBookmarked(memberNo, pno));

        model.addAttribute("dto", placeDTO);
        model.addAttribute("reviews", reviews);
        model.addAttribute("topReviews", topReviews);
    }

    // 5. 맛집 정보 수정 처리 (이미지 파일 업로드 교체 지원)
    @PostMapping("/modify")
    public String modify(
            PageRequestDTO pageRequestDTO,
            PlaceDTO placeDTO,
            @RequestParam(value = "image1File", required = false) MultipartFile image1File,
            @RequestParam(value = "image2File", required = false) MultipartFile image2File,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        String role = (String) session.getAttribute("loginRole");
        if (!"ROLE_ADMIN".equals(role)) {
            redirectAttributes.addFlashAttribute("error", "관리자만 맛집 정보를 수정할 수 있습니다.");
            return "redirect:/place/read?pno=" + placeDTO.getPno();
        }

        // 이미지 파일 업로드 시 새 경로로 업데이트
        if (image1File != null && !image1File.isEmpty()) {
            String uploadedUrl1 = saveUploadedImage(image1File);
            if (uploadedUrl1 != null) {
                placeDTO.setImageUrl1(uploadedUrl1);
            }
        }
        if (image2File != null && !image2File.isEmpty()) {
            String uploadedUrl2 = saveUploadedImage(image2File);
            if (uploadedUrl2 != null) {
                placeDTO.setImageUrl2(uploadedUrl2);
            }
        }

        log.info("PlaceController modify() 호출: " + placeDTO);
        placeService.modify(placeDTO);
        redirectAttributes.addAttribute("pno", placeDTO.getPno());
        redirectAttributes.addFlashAttribute("msg", "맛집 정보가 성공적으로 수정되었습니다.");

        return "redirect:/place/read?" + pageRequestDTO.getLink();
    }

    // 6. 삭제 처리
    @PostMapping("/remove")
    public String remove(Long pno, HttpSession session, RedirectAttributes redirectAttributes) {
        String role = (String) session.getAttribute("loginRole");
        if (!"ROLE_ADMIN".equals(role)) {
            redirectAttributes.addFlashAttribute("error", "관리자만 맛집을 삭제할 수 있습니다.");
            return "redirect:/place/list";
        }

        log.info("PlaceController remove() 호출 - pno: " + pno);
        placeService.remove(pno);
        redirectAttributes.addFlashAttribute("result", "removed");

        return "redirect:/place/list";
    }

    // 7. 리뷰 신규 등록 처리 (사진 업로드 포함)
    @PostMapping("/review/add")
    public String addReview(@RequestParam Long restaurantId,
                            @RequestParam(defaultValue = "5") Integer rating,
                            @RequestParam(defaultValue = "LV1") SoloLevel soloLevel,
                            @RequestParam String content,
                            @RequestParam(value = "file", required = false) MultipartFile file,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        Long memberNo = (Long) session.getAttribute("loginMember");
        if (memberNo == null) {
            redirectAttributes.addFlashAttribute("error", "로그인 후 리뷰를 작성할 수 있습니다.");
            return "redirect:/place/read?pno=" + restaurantId;
        }

        reviewService.addReview(restaurantId, memberNo, rating, soloLevel, content, file);
        redirectAttributes.addFlashAttribute("msg", "리뷰가 성공적으로 등록되었습니다.");

        return "redirect:/place/read?pno=" + restaurantId;
    }

    // 8. 리뷰 수정 처리 (작성자 본인 또는 관리자)
    @PostMapping("/review/modify")
    public String modifyReview(@RequestParam Long reviewId,
                               @RequestParam Long restaurantId,
                               @RequestParam(defaultValue = "5") Integer rating,
                               @RequestParam(defaultValue = "LV1") SoloLevel soloLevel,
                               @RequestParam String content,
                               @RequestParam(value = "file", required = false) MultipartFile file,
                               @RequestParam(value = "deleteImage", defaultValue = "false") boolean deleteImage,
                               @RequestParam(required = false) String from,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        Long memberNo = (Long) session.getAttribute("loginMember");
        String role = (String) session.getAttribute("loginRole");
        boolean isAdmin = "ROLE_ADMIN".equals(role);

        if (memberNo == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/place/read?pno=" + restaurantId;
        }

        boolean success = reviewService.modifyReview(reviewId, memberNo, isAdmin, rating, soloLevel, content, file, deleteImage);
        if (success) {
            redirectAttributes.addFlashAttribute("msg", "리뷰가 성공적으로 수정되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute("error", "리뷰 수정 권한이 없거나 존재하지 않는 리뷰입니다.");
        }

        if ("mypage".equals(from)) {
            return "redirect:/member/mypage";
        }

        return "redirect:/place/read?pno=" + restaurantId;
    }

    // 9. 리뷰 삭제 처리 (작성자 본인 또는 관리자)
    @PostMapping("/review/remove")
    public String removeReview(@RequestParam Long reviewId,
                               @RequestParam Long restaurantId,
                               @RequestParam(required = false) String from,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        Long memberNo = (Long) session.getAttribute("loginMember");
        String role = (String) session.getAttribute("loginRole");
        boolean isAdmin = "ROLE_ADMIN".equals(role);

        if (memberNo == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/place/read?pno=" + restaurantId;
        }

        boolean success = reviewService.deleteReview(reviewId, memberNo, isAdmin);
        if (success) {
            redirectAttributes.addFlashAttribute("msg", "리뷰가 삭제되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute("error", "본인이 작성한 리뷰만 삭제할 수 있습니다.");
        }

        if ("mypage".equals(from)) {
            return "redirect:/member/mypage";
        }

        return "redirect:/place/read?pno=" + restaurantId;
    }

    // 10. 찜(북마크) 토글 처리 (실시간 AJAX API)
    @PostMapping("/bookmark/toggle")
    @ResponseBody
    public ResponseEntity<?> toggleBookmark(@RequestParam Long restaurantId, HttpSession session) {
        Long memberNo = (Long) session.getAttribute("loginMember");
        if (memberNo == null) {
            return ResponseEntity.status(401).body(Map.of("error", "LOGIN_REQUIRED", "message", "로그인이 필요합니다."));
        }

        boolean bookmarked = bookmarkService.toggleBookmark(memberNo, restaurantId);
        long count = bookmarkService.getBookmarkCount(restaurantId);

        return ResponseEntity.ok(Map.of(
                "bookmarked", bookmarked,
                "count", count,
                "message", bookmarked ? "맛집을 찜 목록에 추가했습니다! ❤️" : "찜 목록에서 제외했습니다. 🤍"
        ));
    }

    // 11. 마이페이지에서 찜(북마크) 해제 폼 처리
    @PostMapping("/bookmark/remove")
    public String removeBookmark(@RequestParam Long restaurantId,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Long memberNo = (Long) session.getAttribute("loginMember");
        if (memberNo == null) {
            return "redirect:/member/login";
        }

        bookmarkService.removeBookmark(memberNo, restaurantId);
        redirectAttributes.addFlashAttribute("msg", "찜 목록에서 삭제되었습니다.");

        return "redirect:/member/mypage";
    }

    // 12. 전체 맛집 목록 JSON API (지도 실시간 마커 및 이동 연동용)
    @GetMapping("/api/places")
    @ResponseBody
    public List<PlaceDTO> getAllPlacesApi() {
        return placeService.getAllPlaces();
    }

    // http://localhost:8081/place/map 접속 시 처리
    @GetMapping("/map")
    public String map() {
        return "place/map";
    }

    // http://localhost:8081/place/gacha 접속 시 처리
    @GetMapping("/gacha")
    public String gacha() {
        return "place/gacha";
    }

    // ==========================================
    // 이미지 파일 로컬 저장 헬퍼 메서드
    // ==========================================
    private String saveUploadedImage(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;

        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }
        String storeFileName = UUID.randomUUID().toString() + extension;

        try {
            File dir = new File(uploadPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            Path savePath = Paths.get(uploadPath, storeFileName);
            file.transferTo(savePath.toFile());
            log.info(">>> [맛집 이미지 업로드 저장 완료] 경로: {}", savePath.toAbsolutePath());
            return "/upload/" + storeFileName;
        } catch (IOException e) {
            log.error(">>> [맛집 이미지 업로드 저장 실패]: {}", e.getMessage(), e);
            return null;
        }
    }
}
