package domain.backend.auth.dto.response.user;

import domain.backend.auth.domain.user.Role;

import java.util.UUID;

public class UserChangeRoleResponse {
    private UUID uuid;
    private Role role;
}
