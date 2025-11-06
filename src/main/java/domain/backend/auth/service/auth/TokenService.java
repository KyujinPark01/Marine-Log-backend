package domain.backend.auth.service.auth;

import domain.backend.auth.domain.user.User;
import domain.backend.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class TokenService {
    private final UserRepository userRepository;

//        카카오나 구글, 네이버에 추가적인 정보가 필요하면 메소드 필요
//        public void upsertSocialTokens(User user, Provider provider, String socialAccessToken, Instant socialAccessTokenExpiresAt, String socialRefreshToken, Instant socialRefreshTokenExpiresAt) {
//            SocialLogin social = socialLoginRepository.findByUserAndProvider(user, provider).orElseGet(() -> {
//                SocialLogin s = new SocialLogin();
//                s.setUser(user);
//                s.setProvider(provider);
//                return s;
//            });
//            social.setAccessToken(socialAccessToken);
//            social.setRefreshToken(socialRefreshToken);
//            if (socialAccessTokenExpiresAt != null) {
//                social.setAccessTokenExpiresAt(LocalDateTime.ofInstant(socialAccessTokenExpiresAt, ZoneId.systemDefault()));
//            }
//            if (socialRefreshTokenExpiresAt != null) {
//                social.setRefreshTokenExpiresAt(LocalDateTime.ofInstant(socialRefreshTokenExpiresAt, ZoneId.systemDefault()));
//            }
//            socialLoginRepository.save(social);
//        }
    public void updateRefreshToken(String email, String refreshToken) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다: " + email));
        userRepository.save(user.updateRefreshToken(refreshToken));
    }



}