package domain.backend.auth.dto.response;

import domain.backend.auth.domain.user.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String name;
    private Role role;
}
