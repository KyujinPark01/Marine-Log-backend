package domain.backend.auth.domain.user;

import domain.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "admin_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUser extends BaseTimeEntity {

    @Id
    private Long id; // user의 id와 동일

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // user.id를 PK로 공유
    @JoinColumn(name = "id")
    private User user;

    @Column(length = 50, nullable = false)
    private String role;
}
