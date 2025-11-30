package domain.backend.auth.dto.response.user;

import domain.backend.auth.domain.user.Role;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String name;
    private Role role;
    private UUID uuid;
}
