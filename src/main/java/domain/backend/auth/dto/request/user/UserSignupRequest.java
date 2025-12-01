package domain.backend.auth.dto.request.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserSignupRequest {
    private Long organizationId;
    private String phoneNumber;
    private String address;

}
