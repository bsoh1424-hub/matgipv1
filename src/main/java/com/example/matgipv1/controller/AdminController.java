package com.example.matgipv1.controller;

import com.example.matgipv1.domain.Member;
import com.example.matgipv1.repository.MemberRepository;
import com.example.matgipv1.service.OpenApiService;
import com.example.matgipv1.service.ReviewService;
import dto.ReviewDTO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
@Log4j2
public class AdminController {

    private final MemberRepository memberRepository;
    private final OpenApiService openApiService;
    private final ReviewService reviewService;

    // 관리자 여부 검증 메서드
    private boolean isAdmin(HttpSession session) {
        Long memberNo = (Long) session.getAttribute("loginMember");
        if (memberNo == null) return false;

        Member member = memberRepository.findById(memberNo).orElse(null);
        return member != null && "ROLE_ADMIN".equals(member.getRole());
    }

    // 1. 관리자 대시보드 메인 화면 (/admin/index)
    @GetMapping("/index")
    public String adminIndex(HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/place/list?error=unauthorized";
        }
        return "admin/admin_index";
    }

    // 2. 관리자 회원 관리 목록 화면 (/admin/members)
    @GetMapping("/members")
    public String memberList(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/place/list?error=unauthorized";
        }

        List<Member> memberList = memberRepository.findAll();
        model.addAttribute("members", memberList);

        return "admin/AdminMemberManagement";
    }

    // 3. 회원 정지 (BANNED)
    @PostMapping("/member/ban")
    public String banMember(@RequestParam Long mno, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/place/list?error=unauthorized";

        Member member = memberRepository.findById(mno).orElseThrow();
        member.changeRole("BANNED");
        memberRepository.save(member);
        return "redirect:/admin/members";
    }

    // 4. 회원 정지 해제 (ROLE_USER)
    @PostMapping("/member/unban")
    public String unbanMember(@RequestParam Long mno, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/place/list?error=unauthorized";

        Member member = memberRepository.findById(mno).orElseThrow();
        member.changeRole("ROLE_USER");
        memberRepository.save(member);
        return "redirect:/admin/members";
    }

    // 5. 관리자 리뷰 관리 목록 화면 (/admin/reviews)
    @GetMapping("/reviews")
    public String reviewList(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/place/list?error=unauthorized";
        }

        List<ReviewDTO> reviewList = reviewService.getAllReviewsForAdmin();
        model.addAttribute("reviews", reviewList);

        return "admin/AdminReviewManagement";
    }

    // 6. 관리자 리뷰 숨김(블라인드) / 복원 처리
    @PostMapping("/reviews/hidden")
    public String changeReviewHidden(@RequestParam Long rvno,
                                     @RequestParam boolean hidden,
                                     HttpSession session) {
        if (!isAdmin(session)) return "redirect:/place/list?error=unauthorized";

        reviewService.changeHiddenStatus(rvno, hidden);
        return "redirect:/admin/reviews";
    }

    // 7. 관리자 리뷰 강제 삭제
    @PostMapping("/reviews/remove")
    public String removeReviewByAdmin(@RequestParam Long rvno,
                                      HttpSession session) {
        if (!isAdmin(session)) return "redirect:/place/list?error=unauthorized";

        reviewService.deleteReview(rvno, null, true);
        return "redirect:/admin/reviews";
    }

    // 8. 공공데이터 100건 단위 동기화 API
    @PostMapping("/sync-opendata")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> syncOpenData(@RequestParam(defaultValue = "1") int pageNo,
                                                            @RequestParam(defaultValue = "100") int numOfRows,
                                                            HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(403).body(Map.of("status", "FORBIDDEN", "message", "관리자 권한이 필요합니다."));
        }

        Map<String, Object> responseMap = openApiService.syncOpenData(pageNo, numOfRows);
        return ResponseEntity.ok(responseMap);
    }

    // 9. 공공데이터 100개씩 순회하여 전체 동기화 (기존 데이터 수정 없이 신규만 누적 추가)
    @PostMapping("/sync-opendata-all")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> syncAllOpenData(HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(403).body(Map.of("status", "FORBIDDEN", "message", "관리자 권한이 필요합니다."));
        }

        Map<String, Object> responseMap = openApiService.syncAllOpenData();
        return ResponseEntity.ok(responseMap);
    }
}
