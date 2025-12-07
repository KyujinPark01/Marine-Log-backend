package domain.backend.auth.domain.service.auth;

import domain.backend.auth.domain.user.User;
import domain.backend.auth.dto.response.auth.AuthResponse;
import domain.backend.auth.repository.UserRepository;
import domain.backend.common.security.auth.RefreshTokenStore;
import domain.backend.common.security.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenStore tokenStore;

    public AuthResponse refresh(String refreshToken) {

        // 1. Refresh Token 유효성 검사
        if (!StringUtils.hasText(refreshToken) || !jwtTokenProvider.validate(refreshToken)) {
            throw new IllegalArgumentException("Refresh Token이 유효하지 않습니다.");
        }

        // 2. Refresh Token에서 uuid 추출
        Claims claims = jwtTokenProvider.getClaims(refreshToken);
        String uuid = claims.getSubject();

        // 3. Redis에 저장된 refreshToken과 일치하는지 확인
        String storedToken = tokenStore.get(uuid);
        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new IllegalArgumentException("Refresh Token이 일치하지 않습니다.");
        }

        // 4. 사용자 조회
        User user = userRepository.findByUuid(UUID.fromString(uuid))
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        // 5. Access Token 재발급
        String newAccessToken = jwtTokenProvider.createAccessToken(
                uuid,
                Map.of(
                        "email", user.getEmail(),
                        "name", user.getName(),
                        "role", user.getRole().name() // Enum → String
                )
        );

        // 6. 응답 생성 (refreshToken은 쿠키로 관리 → 응답에서 제외)
        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .isNewUser(false)
                .build();
    }
}
