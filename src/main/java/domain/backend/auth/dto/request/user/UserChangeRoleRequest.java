package domain.backend.auth.dto.request.user;

import domain.backend.auth.domain.user.Role;
import lombok.Getter;

import java.util.UUID;
@Getter
public class UserChangeRoleRequest {
    private Long userId;
    private Role role;

}
