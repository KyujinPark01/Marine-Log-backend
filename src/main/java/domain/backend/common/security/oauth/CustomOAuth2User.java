package domain.backend.common.security.oauth;

import domain.backend.auth.domain.user.User;
import domain.backend.common.security.auth.UserPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public record CustomOAuth2User(User user, Map<String, Object> attributes, boolean isNewUser) implements OAuth2User {

    public UserPrincipal toUserPrincipal() {
        return UserPrincipal.builder()
                .uuid(user.getUuid())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().name())
                .build();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    }

    @Override
    public String getName() {
        return user.getName();
    }
}
