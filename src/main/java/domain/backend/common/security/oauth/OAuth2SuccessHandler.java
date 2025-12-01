package domain.backend.common.security.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.backend.auth.repository.UserRepository;
import domain.backend.common.security.auth.UserPrincipal;
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

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {

        try {
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
            UserPrincipal principal = oAuth2User.toUserPrincipal();

            String uuid = principal.uuid().toString();

            // 🔥 AccessToken 생성
            String accessToken = jwtTokenProvider.createAccessToken(
                    uuid,
                    Map.of(
                            "email", principal.email(),
                            "name", principal.name()
                    )
            );

            // 🔥 RefreshToken은 무조건 새로 발급 (중복 처리 필요 없음)
            String refreshToken = jwtTokenProvider.createRefreshToken(uuid);

            // 🔥 DB 업데이트 강제 수행
            jwtTokenProvider.updateRefreshToken(uuid, refreshToken);

            // 🔥 쿠키에 refreshToken 저장
            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .sameSite("None")
                    .maxAge(14 * 24 * 60 * 60)
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

            // 리다이렉트
            String redirectUrl = "http://localhost:5173/oauth2/success"
                    + "?accessToken=" + accessToken;

            getRedirectStrategy().sendRedirect(request, response, redirectUrl);

        } catch (Exception e) {
            log.error("OAuth2 Success 처리 중 오류", e);
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "OAuth2 처리 실패");
            } catch (Exception ignored) {}
        }
    }


}
