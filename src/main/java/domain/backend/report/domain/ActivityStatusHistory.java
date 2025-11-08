package domain.backend.report.domain;


import domain.backend.activity.domain.Activity;
import domain.backend.auth.domain.user.User;
import domain.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "activity_status_history",
        indexes = {
                @Index(name = "idx_hist_activity_changed_at", columnList = "activity_id, created_at")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ActivityStatusHistory extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 어떤 활동의 상태 변경인지 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "activity_report_id")
    private ActivityReport activityReport;

    /** 이전 상태 (처음이면 null 가능) */
    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private ActivityStatus fromStatus;

    /** 변경된 상태 */
    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private ActivityStatus toStatus;

    /** 변경 사유 */
    @Column(length = 500)
    private String reason;

    /** 상태를 변경한 사용자 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "changed_by")
    private User changedBy;

}
