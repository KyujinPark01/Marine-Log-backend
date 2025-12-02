package domain.backend.auth.domain.service.auth;

import domain.backend.auth.domain.user.User;
import domain.backend.auth.dto.response.auth.AuthResponse;
import domain.backend.auth.repository.UserRepository;
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

    public AuthResponse refresh(String refreshToken) {

        // 1. Refresh Token 유효성 검사
        if (!StringUtils.hasText(refreshToken) || !jwtTokenProvider.validate(refreshToken)) {
            throw new IllegalArgumentException("Refresh Token이 유효하지 않습니다.");
        }

        // 2. 토큰 Claims 추출
        Claims claims = jwtTokenProvider.getClaims(refreshToken);
        String uuid = claims.getSubject();

        // 3. 사용자 조회
        User user = userRepository.findByUuid(UUID.fromString(uuid))
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        // 4. Access Token 재발급
        String newAccessToken = jwtTokenProvider.createAccessToken(
                uuid,
                Map.of(
                        "email", user.getEmail(),
                        "name", user.getName(),
                        "role", user.getRole()
                )
        );

        // 5. 응답 생성
        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .isNewUser(false)
                .build();
    }
}
