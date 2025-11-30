package domain.backend.auth.controller;

import domain.backend.auth.domain.user.Role;
import domain.backend.auth.dto.response.auth.AuthResponse;
import domain.backend.auth.dto.response.user.UserResponse;
import domain.backend.common.dto.ApiResponse;
import domain.backend.common.security.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
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

    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "토큰 갱신", description = "Refresh Token을 이용해 새로운 Access Token을 발급합니다.")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(HttpServletRequest request) {

        String refreshToken = Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                .filter(c -> "refreshToken".equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);

        if (!StringUtils.hasText(refreshToken) || !jwtTokenProvider.validate(refreshToken)) {
            return ResponseEntity.status(401).body(ApiResponse.error("Refresh Token이 유효하지 않습니다."));
        }

        Claims claims = jwtTokenProvider.getClaims(refreshToken);

        String uuid = claims.getSubject();
        String email = claims.get("email", String.class);
        String role = claims.get("role", String.class);
        String name = claims.get("name", String.class);

        String access = jwtTokenProvider.createAccessToken(
                uuid,
                Map.of(
                        "email", email,
                        "name", name,
                        "role", role
                )
        );

        AuthResponse response = AuthResponse.builder()
                .accessToken(access)
                .refreshToken(refreshToken)
                .isNewUser(false)
                .build();

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @Operation(summary = "로그아웃", description = "Refresh Token 쿠키를 제거하여 로그아웃합니다.")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

//    @Operation(summary = "내 정보 조회", description = "Access Token을 통해 로그인한 사용자의 정보를 반환합니다.")
//    @GetMapping("/me")
//    public ResponseEntity<ApiResponse<UserResponse>> me(HttpServletRequest request) {
//
//        Claims claims = (Claims) request.getAttribute("claims");
//
//        UserResponse response = UserResponse.builder()
//                .uuid(UUID.fromString(claims.getSubject()))
//                .email(claims.get("email", String.class))
//                .name(claims.get("name", String.class))
//                .role(Role.valueOf(claims.get("role", String.class)))
//                .build();
//
//        return ResponseEntity.ok(ApiResponse.ok(response));
//    }
}
