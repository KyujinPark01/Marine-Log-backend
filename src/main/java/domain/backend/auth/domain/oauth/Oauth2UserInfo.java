package domain.backend.auth.domain.oauth;

import java.util.Map;

public interface Oauth2UserInfo {
    String getProvider();
    String getProviderId();
    String getEmail();
    String getName();
    Map<String, Object> getAttributes();
}
