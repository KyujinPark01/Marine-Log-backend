package domain.backend.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.backend.auth.domain.oauth.CustomOAuth2User;
import domain.backend.auth.domain.user.Role;
import domain.backend.auth.domain.user.User;
import domain.backend.auth.repository.UserRepository;
import domain.backend.auth.service.user.UserService;
import domain.backend.common.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        try {
            CustomOAuth2User customUser = (CustomOAuth2User) authentication.getPrincipal();
            Role role = Role.valueOf(customUser.getRole());

            String email = customUser.getEmail();

            //  Access Token은 항상 새로 발급
            String accessToken = jwtTokenProvider.createAccessToken(
                    email,
                    Map.of(
                            "uid", customUser.getUser().getUserId(),
                            "name", customUser.getName(),
                            "role", role.name()
                    )
            );

            // Refresh Token 처리 로직
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + email));

            String refreshToken;

            // DB에 기존 RefreshToken이 있고 유효한 경우 → 재사용
            if (user.getRefreshToken() != null && jwtTokenProvider.validate(user.getRefreshToken())) {
                refreshToken = user.getRefreshToken();
            }
            // 없거나 만료된 경우 → 새로 발급 및 DB 갱신
            else {
                refreshToken = jwtTokenProvider.createRefreshToken(email);
                userService.updateRefreshToken(email, refreshToken);
            }

            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .secure(true) // HTTPS 환경에서만 사용
                    .path("/")
                    .sameSite("None") // 다른 도메인(Vue 프론트)에서도 쿠키 전송 가능
                    .maxAge(14 * 24 * 60 * 60) // 14일
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

            // 프론트로 리다이렉트
            String redirectUrl = "http://localhost:5173/oauth2/success"
                    + "?accessToken=" + accessToken;

            getRedirectStrategy().sendRedirect(request, response, redirectUrl);

        } catch (Exception e) {
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "OAuth2 처리 실패");
            } catch (Exception ignored) {}
        }
    }
}
