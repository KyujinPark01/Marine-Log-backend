package domain.backend.auth.dto.request.admin;

import lombok.Getter;

@Getter
public class AdminLoginRequest {
    private String email;
    private String password;
}
