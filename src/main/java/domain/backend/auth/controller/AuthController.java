package domain.backend.auth.controller;

import domain.backend.auth.controller.dto.response.AuthResponse;
import domain.backend.auth.controller.dto.response.UserResponse;
import domain.backend.auth.domain.user.User;
import domain.backend.auth.repository.UserRepository;
import domain.backend.common.security.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(HttpServletRequest request) {
        String refreshToken = Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                .filter(c -> "refreshToken".equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);

        if (!StringUtils.hasText(refreshToken) || !jwtTokenProvider.validate(refreshToken)) {
            return ResponseEntity.status(401).build();
        }
        String email = jwtTokenProvider.getSubject(refreshToken);
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.status(401).build();

        String access = jwtTokenProvider.createAccessToken(
                email,
                Map.of("uid", user.getUserId(),
                        "name", user.getName(),
                        "role", user.getRole().name())
        );

        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(access)
                .refreshToken(refreshToken)
                .isNewUser(false)
                .build());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody(required = false) Map<String, String> body) {
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(HttpServletRequest request, @RequestHeader(value = "Authorization", required = false) String auth) {
        if (auth == null || !auth.startsWith("Bearer "))
            return ResponseEntity.status(401).build();
        String token = auth.substring(7);
        if (!jwtTokenProvider.validate(token))
            return ResponseEntity.status(401).build();

        Claims claims = jwtTokenProvider.getClaims(token);
        String email = claims.getSubject();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.status(401).build();

        return ResponseEntity.ok(UserResponse.builder()
                .id(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .build());
    }
}
