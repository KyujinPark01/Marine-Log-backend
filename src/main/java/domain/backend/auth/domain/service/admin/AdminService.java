package domain.backend.auth.domain.service.admin;

import domain.backend.auth.domain.user.Role;
import domain.backend.auth.domain.user.User;
import domain.backend.auth.dto.request.admin.AdminLoginRequest;
import domain.backend.auth.dto.request.user.UserChangeRoleRequest;
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
                admin.getUserId(),
                refreshToken,
                jwtTokenProvider.getRefreshExpSeconds()
        );

        log.info("ADMIN 로그인 성공 → Redis 저장 refresh:{}, uuid={}",refreshToken, admin.getUserId());

        return new AdminLoginResponse(accessToken, refreshToken);
    }
    @Transactional
    public void changeRole(UserChangeRoleRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        user.updateRole(request.getRole());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {

            if (principal.userId().equals(user.getUserId())) {

                UserPrincipal newPrincipal = UserPrincipal.from(user);

                Authentication newAuth = new UsernamePasswordAuthenticationToken(
                        newPrincipal,
                        null,
                        newPrincipal.getAuthorities()
                );

                SecurityContextHolder.getContext().setAuthentication(newAuth);

                log.info("🔄 SecurityContext 권한 갱신 완료: {}", newPrincipal.getAuthorities());
            }
        }

    }

}