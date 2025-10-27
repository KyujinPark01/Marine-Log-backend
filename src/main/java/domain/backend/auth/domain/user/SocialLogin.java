package domain.backend.auth.domain.user;

import domain.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "social_login",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"provider", "providerId"})
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialLogin extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Provider provider; // ENUM(네이버, 카카오, 구글)

    @Column(length = 255)
    private String providerId;

    private LocalDateTime refreshTokenExpiresAt;

    // user 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public SocialLogin(Provider provider,
                       String providerId,
                       LocalDateTime refreshTokenExpiresAt,
                       User user) {
        this.provider = provider;
        this.providerId = providerId;
        this.refreshTokenExpiresAt = refreshTokenExpiresAt;
        this.user = user;
    }
}