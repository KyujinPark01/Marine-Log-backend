package domain.backend.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.backend.auth.controller.dto.response.AuthResponse;
import domain.backend.auth.domain.oauth.CustomOAuth2User;
import domain.backend.auth.domain.user.Role;
import domain.backend.auth.repository.UserRepository;
import domain.backend.auth.service.user.UserService;
import domain.backend.common.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
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
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {
            CustomOAuth2User customUser = (CustomOAuth2User) authentication.getPrincipal();
            Role role = Role.valueOf(customUser.getRole());

            String access = jwtTokenProvider.createAccessToken(
                    customUser.getEmail(),
                    Map.of(
                            "uid", customUser.getUser().getId(),
                            "name", customUser.getName(),
                            "role", role.name()
                    )
            );
            String refresh = jwtTokenProvider.createRefreshToken(customUser.getEmail());

            userRepository.findByEmail(customUser.getEmail()).ifPresent(user -> {
                userService.updateRefreshToken(user.getEmail(), refresh);
            });

            AuthResponse body = AuthResponse.builder()
                    .accessToken(access)
                    .refreshToken(refresh)
                    .isNewUser(customUser.isNewUser())
                    .build();

            response.setStatus(HttpServletResponse.SC_OK);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(body));

        } catch (Exception e) {
            log.error("OAuth2SuccessHandler Error", e);
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "OAuth2 처리 실패");
            } catch (Exception ignored) {}
        }
    }
}
