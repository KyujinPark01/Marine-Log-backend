package domain.backend.auth.domain.user;

import domain.backend.common.entity.BaseTimeEntity;
import domain.backend.organization.domain.entity.Organization;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;


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

    @Column(length = 255)
    private String address;

    @Column(name = "phone_number", length = 30)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(name = "refresh_token", length = 500)
    private String refreshToken;

    private LocalDateTime refreshTokenExpiresAt;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @Builder
    public User (Long userId,
                 String email,
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
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.refreshToken = refreshToken;
        this.refreshTokenExpiresAt = refreshTokenExpiresAt;
        this.organization = organization;
    }

    public User updateRefreshToken(String refreshToken)
    {
        return User.builder()
                .userId(this.userId)
                .email(this.email)
                .name(this.name)
                .address(this.address)
                .phoneNumber(this.phoneNumber)
                .role(this.role)
                .refreshToken(refreshToken)
                .refreshTokenExpiresAt(this.refreshTokenExpiresAt)
                .build();


    }


}
