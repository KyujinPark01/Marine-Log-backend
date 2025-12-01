package domain.backend.auth.dto.response.admin;

import lombok.Getter;

@Getter
public class AdminLoginResponse {
    private String accessToken;
    private String refreshToken;

    public AdminLoginResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
