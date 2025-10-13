package domain.backend.auth.domain.user;

import domain.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255, unique = true)
    private String email;

    @Column(length = 100)
    private String name;

    @Column(length = 255)
    private String address;

    @Column(length = 30)
    private String phoneNumber;

    //    // Organization 연결
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "organization_id")
//    private Organization organization;


    // 소셜 로그인 정보 (1:N)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SocialLogin> socialLogins;

    // 관리자 정보 (1:1)
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private AdminUser adminUser;
}
