package domain.backend.auth.domain.service.admin;

import domain.backend.auth.domain.user.Role;
import domain.backend.auth.domain.user.User;
import domain.backend.auth.dto.request.admin.AdminLoginRequest;
import domain.backend.auth.dto.request.user.UserChangeRoleRequest;
import domain.backend.auth.dto.response.TokenResponse;
import domain.backend.auth.dto.response.admin.AdminLoginResponse;
import domain.backend.auth.repository.UserRepository;
import domain.backend.common.security.auth.RefreshTokenStore;
import domain.backend.common.security.auth.UserPrincipal;
import domain.backend.common.security.jwt.JwtTokenProvider;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenStore tokenStore;
    private final RedisTemplate<String, String> redisTemplate;

    @PostConstruct
    public void testRedis() {
        try {
            redisTemplate.opsForValue().set("test-connection", "ok");
            log.info("🚀 Redis TEST 저장 성공");
        } catch (Exception e) {
            log.error("❌ Redis 저장 실패", e);
        }
    }
    public AdminLoginResponse login(AdminLoginRequest adminLoginRequest) {

        // 1) 이메일 검증
        User admin = userRepository.findByEmailAndRole(adminLoginRequest.getEmail(), Role.ADMIN)
                .orElseThrow(() -> new IllegalArgumentException("찾을 수 없음"));

        // 2) 비밀번호 검증
        if (!passwordEncoder.matches(adminLoginRequest.getPassword(), admin.getPassword())) {
            throw new IllegalArgumentException("비밀번호 틀림");
        }

        // 3) uuid 문자열 추출
        String uuid = admin.getUuid().toString();

        // 4) Access Token 생성
        String accessToken = jwtTokenProvider.createAccessToken(
                uuid,
                Map.of(
                        "email", admin.getEmail(),
                        "role", admin.getRole()
                )
        );

        // 5) Refresh Token 생성
        String refreshToken = jwtTokenProvider.createRefreshToken(uuid);

        // 6) Redis 저장
        tokenStore.save(
                String.valueOf(admin.getUserId()),
                refreshToken,
                jwtTokenProvider.getRefreshExpSeconds()
        );

        log.info("ADMIN 로그인 성공 → Redis 저장 refresh:{}, uuid={}",refreshToken, admin.getUserId());

        return new AdminLoginResponse(accessToken, refreshToken);
    }
    @Transactional
    public TokenResponse changeRole(UserChangeRoleRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        // 1) ROLE 업데이트
        user.updateRole(request.getRole());

        // 2) 현재 로그인 유저인지 확인
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isSelfChange = auth != null &&
                auth.getPrincipal() instanceof UserPrincipal principal &&
                principal.userId().equals(user.getUserId());

        // 3) 본인이 직접 role을 바꾼 경우 -> SecurityContext + JWT 모두 재발급
        if (isSelfChange) {

            UserPrincipal newPrincipal = UserPrincipal.from(user);

            // SecurityContext 업데이트
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    newPrincipal,
                    null,
                    newPrincipal.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(newAuth);

            log.info("🔄 SecurityContext 권한 갱신: {}", newPrincipal.getAuthorities());

            // ==== 🔥 새 토큰 생성 (JwtTokenProvider 버전 맞춤) ====
            Map<String, Object> claims = Map.of(
                    "role", user.getRole().name()
            );

            String newAccessToken =
                    jwtTokenProvider.createAccessToken(String.valueOf(user.getUuid()), claims);

            String newRefreshToken =
                    jwtTokenProvider.createRefreshToken(String.valueOf(user.getUuid()));

            // refreshToken 저장 (Redis 또는 DB)
            tokenStore.save(
                    String.valueOf(user.getUserId()),
                    newRefreshToken,
                    jwtTokenProvider.getRefreshExpSeconds()
            );

            return new TokenResponse(newAccessToken, newRefreshToken);
        }

        // 다른 유저의 권한 변경 → 토큰 불필요
        return null;
    }


}