package domain.backend.auth.controller;

import domain.backend.auth.domain.service.auth.TokenService;
import domain.backend.auth.dto.response.auth.AuthResponse;
import domain.backend.common.dto.ApiResponse;
import domain.backend.common.security.auth.RefreshTokenStore;
import domain.backend.common.security.auth.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "Auth API", description = "로그인, 토큰 갱신, 사용자 인증 관련 API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final TokenService tokenService;
    private final RefreshTokenStore tokenStore;
    @Operation(summary = "토큰 갱신", description = "Refresh Token을 이용해 새로운 Access Token을 발급합니다.")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(HttpServletRequest request) {

        String refreshToken = Arrays.stream(
                        Optional.ofNullable(request.getCookies()).orElse(new Cookie[0])
                )
                .filter(c -> "refreshToken".equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);

        AuthResponse response = tokenService.refresh(refreshToken);


        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @Operation(summary = "로그아웃", description = "Refresh Token 쿠키를 제거하여 로그아웃합니다.")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal UserPrincipal principal) {

        tokenStore.delete(principal.userId());

        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .maxAge(0)
                .path("/")
                .sameSite("None")
                .secure(true)
                .httpOnly(true)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body(ApiResponse.ok(null));
    }
}
