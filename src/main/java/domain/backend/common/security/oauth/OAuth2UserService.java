package domain.backend.common.security.oauth;

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

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final SocialLoginRepository socialLoginRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String providerId = userRequest.getClientRegistration().getRegistrationId();
        Provider provider = Provider.valueOf(providerId.toUpperCase());

        Oauth2UserInfo userInfo = switch (provider) {
            case GOOGLE -> new GoogleUserInfo(oAuth2User.getAttributes());
            case NAVER -> new NaverUserInfo(oAuth2User.getAttributes());
            case KAKAO -> new KakaoUserInfo(oAuth2User.getAttributes());
        };

        User user = userRepository.findByEmail(userInfo.getEmail()).orElse(null);
        boolean isNewUser = false;

        if (user == null) {
            user = User.builder()
                    .email(userInfo.getEmail())
                    .name(userInfo.getName())
                    .role(Role.USER)
                    .build();
            userRepository.save(user);

            isNewUser = true;

            saveSocialLogin(user, provider, userInfo.getProviderId());

            return new CustomOAuth2User(user, oAuth2User.getAttributes(), isNewUser);
        }

        boolean hasSocial = socialLoginRepository
                .findByUserAndProvider(user, provider)
                .isPresent();

        if (!hasSocial) {
            saveSocialLogin(user, provider, userInfo.getProviderId());
        }

        return new CustomOAuth2User(user, oAuth2User.getAttributes(), isNewUser);
    }

    private void saveSocialLogin(User user, Provider provider, String providerId) {
        SocialLogin socialLogin = SocialLogin.builder()
                .provider(provider)
                .providerId(providerId)
                .user(user)
                .build();

        socialLoginRepository.save(socialLogin);
    }
}
