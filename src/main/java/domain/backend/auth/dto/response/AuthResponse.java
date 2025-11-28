package domain.backend.auth.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private boolean isNewUser;
}
