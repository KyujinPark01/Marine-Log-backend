package domain.backend.report.domain.entity;

import domain.backend.common.entity.BaseTimeEntity;
import domain.backend.organization.domain.entity.Organization;
import domain.backend.report.domain.enums.CollaborationStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Collaboration extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 제보(ActivityReport)에 대한 연합인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_report_id", nullable = false)
    private ActivityReport activityReport;

    // 연합을 요청한 단체
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_org_id", nullable = false)
    private Organization hostOrg;

    // 연합 요청을 받은 단체
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_org_id", nullable = false)
    private Organization targetOrg;

    // 요청 상태 (요청됨 / 수락됨 / 거절됨)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CollaborationStatus status;

    public void approve() {
        this.status = CollaborationStatus.APPROVED;
    }

    public void reject() {
        this.status = CollaborationStatus.REJECTED;
    }
    public void updateStatus(CollaborationStatus status) {
        this.status = status;
    }
}
