package domain.backend.auth.controller;

import domain.backend.auth.domain.user.Role;
import domain.backend.auth.domain.user.User;
import domain.backend.auth.dto.response.AuthResponse;
import domain.backend.auth.dto.response.UserResponse;
import domain.backend.auth.repository.UserRepository;
import domain.backend.common.security.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@Tag(name = "Auth API", description = "로그인, 토큰 갱신, 사용자 인증 관련 API")

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtTokenProvider jwtTokenProvider;


    @Operation(summary = "토큰 갱신", description = "Refresh Token을 이용해 새로운 Access Token을 발급합니다.")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(HttpServletRequest request) {
        // 1.쿠키에서 refreshToken 호출
        String refreshToken = Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                .filter(c -> "refreshToken".equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);

        // 2.refreshToken 유효성 검사
        if (!StringUtils.hasText(refreshToken) || !jwtTokenProvider.validate(refreshToken)) {
            return ResponseEntity.status(401).build();
        }

        Claims claims = jwtTokenProvider.getClaims(refreshToken);

        String email = claims.getSubject();
        Long uid = claims.get("uid", Long.class);
        String name = claims.get("name", String.class);
        String role = claims.get("role", String.class);

        String access = jwtTokenProvider.createAccessToken(
                email,
                Map.of(
                        "uid", uid,
                        "name", name,
                        "role", role
                )
        );

        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(access)
                .refreshToken(refreshToken)
                .isNewUser(false)
                .build());
    }

    @Operation(summary = "로그아웃", description = "서버 측 세션을 종료합니다.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "내 정보 조회", description = "Access Token을 통해 로그인한 사용자의 정보를 반환합니다.")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@RequestHeader(value = "Authorization", required = false) String auth) {

        if (auth == null || !auth.startsWith("Bearer "))
            return ResponseEntity.status(401).build();

        String token = auth.substring(7);

        if (!jwtTokenProvider.validate(token))
            return ResponseEntity.status(401).build();

        Claims claims = jwtTokenProvider.getClaims(token);

        return ResponseEntity.ok(
                UserResponse.builder().
                        id(claims.get("uid", Long.class))
                        .email(claims.getSubject())
                        .name(claims.get("name", String.class))
                        .role(Role.valueOf(claims.get("role", String.class)))
                        .build());
    }
}
