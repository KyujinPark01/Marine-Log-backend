package domain.backend.auth.domain.service.admin;

import domain.backend.auth.domain.user.Role;
import domain.backend.auth.domain.user.User;
import domain.backend.auth.dto.request.admin.AdminLoginRequest;
import domain.backend.auth.dto.response.admin.AdminLoginResponse;
import domain.backend.auth.repository.UserRepository;
import domain.backend.common.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminAuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    public AdminLoginResponse login(AdminLoginRequest adminLoginRequest){
        //이메일 검증
        User admin = userRepository.findByEmailAndRole(adminLoginRequest.getEmail(), Role.ADMIN)
                .orElseThrow(()->new IllegalArgumentException("찾을 수 없음"));
        //비밀번호 검증
        if(!passwordEncoder.matches(adminLoginRequest.getPassword(), admin.getPassword())){
            throw new IllegalArgumentException("비밀번호 틀림");
        }

        // 3) uuid 추출
        String uuid = admin.getUuid().toString();
        //토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(
                uuid,
                Map.of(
                        "email", admin.getEmail(),
                        "role", Role.ADMIN.name()
                )
        );
        String refreshToken;
        if (admin.getRefreshToken() != null && jwtTokenProvider.validate(admin.getRefreshToken())) {
            refreshToken = admin.getRefreshToken();
        } else {
            refreshToken = jwtTokenProvider.createRefreshToken(uuid);
            jwtTokenProvider.updateRefreshToken(uuid, refreshToken);
        }

        return new AdminLoginResponse(accessToken, refreshToken);
    }
}
