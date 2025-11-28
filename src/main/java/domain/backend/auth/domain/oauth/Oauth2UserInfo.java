package domain.backend.auth.domain.oauth;

import java.util.Map;

public interface Oauth2UserInfo {
    String getProviderId();
    String getEmail();
    String getName();
}
