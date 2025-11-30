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

            String accessToken = jwtTokenProvider.createAccessToken(
                    uuid,
                    Map.of(
                            "email", principal.email(),
                            "name", principal.name(),
                            "role", principal.role()
                    )
            );

            String refreshToken = principal.refreshToken();
            if (refreshToken == null || !jwtTokenProvider.validate(refreshToken)) {
                refreshToken = jwtTokenProvider.createRefreshToken(uuid);
                jwtTokenProvider.updateRefreshToken(uuid, refreshToken);
            }

            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .sameSite("None")
                    .maxAge(14 * 24 * 60 * 60)
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

            String redirectUrl = "http://localhost:5173/oauth2/success"
                    + "?accessToken=" + accessToken;

            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
            log.info("OAuth2 Principal = {}", authentication.getPrincipal().getClass().getName());
            log.info("OAuth2 Principal Details = {}", authentication.getPrincipal());
        } catch (Exception e) {
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "OAuth2 처리 실패");
            } catch (Exception ignored) {}
        }

    }

}
