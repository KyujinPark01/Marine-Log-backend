package domain.backend.auth.domain.user;

import domain.backend.common.entity.BaseTimeEntity;
import domain.backend.organization.domain.entity.Organization;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "member")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
        // Organization 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;


    // 소셜 로그인 정보 (1:N)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SocialLogin> socialLogins;
}
