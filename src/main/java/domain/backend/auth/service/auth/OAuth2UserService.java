package domain.backend.auth.service.auth;
import domain.backend.auth.domain.oauth.*;
import domain.backend.auth.domain.user.Provider;
import domain.backend.auth.domain.user.Role;
import domain.backend.auth.domain.user.SocialLogin;
import domain.backend.auth.domain.user.User;
import domain.backend.auth.repository.SocialLoginRepository;
import domain.backend.auth.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import static domain.backend.auth.domain.user.Provider.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    private final SocialLoginRepository socialLoginRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();
        log.info("시작 {} ", provider);
        Oauth2UserInfo userInfo = switch (provider) {
            case "google" -> new GoogleUserInfo(oAuth2User.getAttributes());
            case "naver" -> new NaverUserInfo(oAuth2User.getAttributes());
            case "kakao" -> new KakaoUserInfo(oAuth2User.getAttributes());
            default -> throw new IllegalArgumentException("정해지지 않은 provider" + provider);
        };
        log.info("userInfo : {}", userInfo);

        User user = userRepository.findByEmail(userInfo.getEmail()).orElse(null);
        boolean isNewUser = false;

        if (user == null) {
            user = User.builder()
                    .email(userInfo.getEmail())
                    .name(userInfo.getName())
                    .role(Role.USER)
                    .build();
            userRepository.save(user);

            SocialLogin socialLogin = SocialLogin.builder()
                    .provider(valueOf(provider.toUpperCase()))
                    .providerId(userInfo.getProviderId())
                    .user(user)
                    .build();

            socialLoginRepository.save(socialLogin);
        }  boolean hasSocial = socialLoginRepository.findByUserAndProvider(user, valueOf(provider.toUpperCase())).isPresent();
        if (!hasSocial) {
            SocialLogin socialLogin = SocialLogin.builder()
                    .provider(valueOf(provider.toUpperCase()))
                    .providerId(userInfo.getProviderId())
                    .user(user)
                    .build();
            socialLoginRepository.save(socialLogin);
        }


        return new CustomOAuth2User(user, oAuth2User.getAttributes(), isNewUser);
    }
}
