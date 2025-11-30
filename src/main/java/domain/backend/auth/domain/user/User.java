package domain.backend.auth.domain.user;

import domain.backend.common.entity.BaseTimeEntity;
import domain.backend.organization.domain.entity.Organization;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(length = 255, unique = true)
    private String email;

    @Column(length = 100)
    private String name;

    private String password;

    @Column(length = 255)
    private String address;

    @Column(name = "phone_number", length = 30)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(name = "refresh_token", length = 500)
    private String refreshToken;
    @Column(name = "refresh_token_expires_at")
    private LocalDateTime refreshTokenExpiresAt;

    @Column(unique = true, nullable = false, updatable = false)
    private UUID uuid  = UUID.randomUUID();

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;



    @Builder
    public User (Long userId,
                 String email,
                 String password,
                 String name,
                 String address,
                 String phoneNumber,
                 Role role,
                 String refreshToken,
                 LocalDateTime refreshTokenExpiresAt,
                 Organization organization
                 ) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.refreshToken = refreshToken;
        this.refreshTokenExpiresAt = refreshTokenExpiresAt;
        this.organization = organization;
    }

    public void updateRefreshToken(String refreshToken, LocalDateTime expiresAt) {
        this.refreshToken = refreshToken;
        this.refreshTokenExpiresAt = expiresAt;
    }
    public void updateAdditionalInfo(Organization organization, String phoneNumber, String address) {
        this.organization = organization;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }


}
