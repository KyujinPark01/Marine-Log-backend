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
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialLogin extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Provider provider; // ENUM(네이버, 카카오, 구글)

    @Column(length = 1000)
    private String accessToken;

    @Column(length = 1000)
    private String refreshToken;

    @Column(length = 255)
    private String providerId;

    private LocalDateTime accessTokenExpiresAt;
    private LocalDateTime refreshTokenExpiresAt;


    // user 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
