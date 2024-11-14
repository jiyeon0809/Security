package com.example.tobi.securityproject.controller;

import com.example.tobi.securityproject.Util.CookieUtil;
import com.example.tobi.securityproject.dto.*;
import com.example.tobi.securityproject.model.Member;
import com.example.tobi.securityproject.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping("/join")
    public ResponseEntity<SIgnUpResponseDTO> signUp(@RequestBody SignUpRequestDTO signUpRequestDTO) {
        memberService.signUp(signUpRequestDTO.toMember(bCryptPasswordEncoder));
        return ResponseEntity.ok(
                SIgnUpResponseDTO.builder()
                        .message("회원가입이 완료되었습니다.\n 다시로그인해주세요")
                        .url("/member/join")
                        .build()
        );
    }

    @PostMapping("/login")
    public ResponseEntity<SignInResponseDTO> signIn(
            @RequestBody SignInRequestDTO signInRequestDTO,
            HttpServletResponse response
    ) {
        SignInResponseDTO signInResponseDTO =  memberService.signIn(signInRequestDTO.getUserId(), signInRequestDTO.getPassword());

        CookieUtil.addCookie(response, "refreshToken", signInResponseDTO.getRefreshToken(), 3*24*60*60);
        System.out.println("signInResponseDTO.getRefreshToken() :: " + signInResponseDTO.getRefreshToken());
        signInResponseDTO.setRefreshToken(null);

        return ResponseEntity.ok(signInResponseDTO);
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponseDTO> logout(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.deleteCookie(request, response, "refreshToken");
        return ResponseEntity.ok(
                LogoutResponseDTO.builder()
                        .message("로그아웃 되었습니다.")
                        .url("/member/join")
                        .build()
        );
    }

    @GetMapping("/user/info")
    public ResponseEntity<UserInfoResponseDTO> getUserInfo(HttpServletRequest request) {
        Member member = (Member) request.getAttribute("member");
        return ResponseEntity.ok(
                UserInfoResponseDTO.builder()
                        .id(member.getId())
                        .userId(member.getUserId())
                        .userName(member.getUserName())
                        .role(member.getRole())
                        .build()
        );
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/user")
    public void userpage() {
        System.out.println("user page");
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin")
    public void adminpage() {
        System.out.println("admin page");
    }

}
