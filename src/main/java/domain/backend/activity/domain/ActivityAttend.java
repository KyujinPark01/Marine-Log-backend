package domain.backend.activity.domain;

import domain.backend.auth.domain.user.User;
import domain.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "activity_attend")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityAttend extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean isAccepted;
    private Boolean isAttended;
    private LocalDate appliedAt;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id")
    private Activity activity;

    @OneToOne(mappedBy = "attend", cascade = CascadeType.ALL, orphanRemoval = true)
    private ActivityReview review;
}