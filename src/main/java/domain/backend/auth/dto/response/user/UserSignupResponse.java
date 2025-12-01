package domain.backend.auth.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserSignupResponse {
    private Long organizationId;
    private String phoneNumber;
    private String address;

}
