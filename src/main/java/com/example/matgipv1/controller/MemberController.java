package com.example.matgipv1.controller;

import com.example.matgipv1.domain.Member;
import com.example.matgipv1.service.BookmarkService;
import com.example.matgipv1.service.LogDataService;
import com.example.matgipv1.service.MemberService;
import com.example.matgipv1.service.ReviewService;
import dto.BookmarkDTO;
import dto.MemberJoinDTO;
import dto.MemberLoginDTO;
import dto.MyPageDTO;
import dto.ReviewDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/member")
@Log4j2
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final ReviewService reviewService;
    private final BookmarkService bookmarkService;
    private final LogDataService logDataService;

    // 1. 회원가입 화면
    @GetMapping("/register")
    public void registerGET() {
    }

    // 2. 회원가입 처리
    @PostMapping("/register")
    public String registerPOST(MemberJoinDTO memberJoinDTO, HttpServletRequest request, Model model) {
        try {
            memberService.join(memberJoinDTO);
            logDataService.logActivity(request, "REGISTER", "일반 회원가입 성공: " + memberJoinDTO.getEmail(), null, null, null, "email=" + memberJoinDTO.getEmail());
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            logDataService.logActivity(request, "REGISTER_FAILED", "회원가입 실패: " + e.getMessage(), null, null, null, "email=" + memberJoinDTO.getEmail());
            return "member/register";
        }
        return "redirect:/member/login";
    }

    // 3. 로그인 화면
    @GetMapping("/login")
    public void loginGET() {
    }

    // 4. 로그인 처리 (일반 세션 로그인)
    @PostMapping("/login")
    public String loginPOST(MemberLoginDTO memberLoginDTO, HttpServletRequest request, HttpSession session, Model model) {
        try {
            Member member = memberService.login(memberLoginDTO);

            log.info("로그인 성공 회원: " + member.getEmail() + " [Role: " + member.getRole() + "]");

            session.setAttribute("loginMember", member.getMno());
            session.setAttribute("nickname", member.getNickname());
            session.setAttribute("loginRole", member.getRole());

            // 일반 로그인 로그 기록
            logDataService.logActivity(request, "LOGIN_LOCAL", "일반 로그인 성공 (" + member.getEmail() + ")", member.getMno(), member.getNickname(), null, "role=" + member.getRole());

            // 관리자 계정(ROLE_ADMIN) 로그인 시 관리자 센터로 이동
            if ("ROLE_ADMIN".equals(member.getRole())) {
                return "redirect:/admin/index";
            }

            return "redirect:/place/list";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            logDataService.logActivity(request, "LOGIN_FAILED", "일반 로그인 실패 (" + memberLoginDTO.getEmail() + "): " + e.getMessage(), null, null, null, "email=" + memberLoginDTO.getEmail());
            return "member/login";
        }
    }

    // 5. 로그아웃 처리
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, HttpSession session, Authentication authentication) {
        Long mno = (Long) session.getAttribute("loginMember");
        String nickname = (String) session.getAttribute("nickname");
        logDataService.logActivity(request, "LOGOUT", "사용자 로그아웃", mno, nickname, null, null);

        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        session.invalidate();
        return "redirect:/place/list";
    }

    // 6. 마이페이지 (회원 정보 + 작성 리뷰 관리 + 찜한 맛집 목록)
    @GetMapping("/mypage")
    public String myPage(HttpSession session, Authentication authentication, Model model) {
        Long mno = getLoggedInMno(session, authentication);

        if (mno == null) {
            return "redirect:/member/login";
        }

        MyPageDTO dto = memberService.getMyPage(mno);
        List<ReviewDTO> myReviews = reviewService.getReviewsByMember(mno);
        List<BookmarkDTO> myBookmarks = bookmarkService.getMyBookmarks(mno);

        model.addAttribute("dto", dto);
        model.addAttribute("myReviews", myReviews);
        model.addAttribute("myBookmarks", myBookmarks);

        return "member/mypage";
    }

    // 7. 회원 정보 수정
    @PostMapping("/modify")
    public String modify(
            HttpServletRequest request,
            HttpSession session,
            Authentication authentication,
            @RequestParam String nickname,
            @RequestParam(required = false) String newPassword,
            @RequestParam(required = false) String profileImage
    ) {
        Long mno = getLoggedInMno(session, authentication);

        if (mno == null) {
            return "redirect:/member/login";
        }

        memberService.modify(mno, nickname, newPassword, profileImage);
        session.setAttribute("nickname", nickname);

        logDataService.logActivity(request, "MODIFY_PROFILE", "회원정보(닉네임/비밀번호) 수정", mno, nickname, null, "newNickname=" + nickname);

        return "redirect:/member/mypage";
    }

    // 8. 회원 탈퇴 (진짜 member DB에서 찾아 완전 삭제 및 세션/보안컨텍스트 정리)
    @PostMapping("/withdraw")
    public String withdraw(
            HttpServletRequest request,
            HttpServletResponse response,
            HttpSession session,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        Long mno = getLoggedInMno(session, authentication);
        String nickname = (String) session.getAttribute("nickname");

        log.info(">>> [/member/withdraw 요청 진입] 로그인 회원 mno: {}", mno);

        if (mno == null) {
            log.warn(">>> [/member/withdraw] 로그인 상태가 아니어서 탈퇴 불가");
            return "redirect:/member/login";
        }

        try {
            logDataService.logActivity(request, "WITHDRAW", "회원 자진 탈퇴 (mno: " + mno + ", nickname: " + nickname + ")", mno, nickname, null, null);
            
            // 1. 회원 및 연관 데이터 실제 DB 삭제
            memberService.withdraw(mno);
            
            // 2. Spring Security 로그아웃 및 세션 무효화
            if (authentication != null) {
                new SecurityContextLogoutHandler().logout(request, response, authentication);
            }
            session.invalidate();
            SecurityContextHolder.clearContext();

            log.info(">>> [/member/withdraw] 회원 mno: {} 탈퇴 및 세션 초기화 완료", mno);
            redirectAttributes.addFlashAttribute("msg", "회원 탈퇴가 성공적으로 완료되었습니다.");
        } catch (Exception e) {
            log.error(">>> [/member/withdraw] 회원 탈퇴 처리 중 에러 발생: ", e);
            redirectAttributes.addFlashAttribute("error", "회원 탈퇴 처리 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/member/mypage";
        }

        return "redirect:/place/list";
    }

    // ==========================================
    // 세션 또는 SecurityContext에서 현재 로그인한 사용자의 MNO 안전 조회
    // ==========================================
    private Long getLoggedInMno(HttpSession session, Authentication authentication) {
        if (session != null && session.getAttribute("loginMember") != null) {
            return (Long) session.getAttribute("loginMember");
        }

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            String email = null;

            if (principal instanceof OAuth2User) {
                OAuth2User oauthUser = (OAuth2User) principal;
                Map<String, Object> attributes = oauthUser.getAttributes();
                if (attributes.get("response") != null) {
                    Map<String, Object> resp = (Map<String, Object>) attributes.get("response");
                    email = (String) resp.get("email");
                    if (email == null && resp.get("id") != null) {
                        email = "naver_" + resp.get("id") + "@naver.social";
                    }
                } else if (attributes.get("id") != null) {
                    Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                    if (kakaoAccount != null && kakaoAccount.get("email") != null) {
                        email = (String) kakaoAccount.get("email");
                    }
                    if (email == null) {
                        email = "kakao_" + attributes.get("id") + "@kakao.social";
                    }
                }
            } else if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                email = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
            }

            if (email != null) {
                Member member = memberService.findByEmail(email).orElse(null);
                if (member != null) {
                    if (session != null) {
                        session.setAttribute("loginMember", member.getMno());
                        session.setAttribute("nickname", member.getNickname());
                        session.setAttribute("loginRole", member.getRole());
                    }
                    return member.getMno();
                }
            }
        }

        return null;
    }
}
