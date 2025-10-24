package domain.backend.organization.domain.entity;

import domain.backend.activity.domain.Activity;
import jakarta.persistence.*;
import lombok.*;

/*
 *  N:M 문제를 해결하기 위해 생성한 활동 참여 단체 엔티티
 */

@Entity
@Table(name = "hosted_by",
        uniqueConstraints = @UniqueConstraint(
                name="uk_hosted_by_pair",
                columnNames={"activity_id","org_id"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HostedBy {

    // 1. Embeddable을 사용한 PFK
    @EmbeddedId
    private HostedById hostedById;

    // 2. organization의 FK 선언
    // activity 1 : N waste
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("orgId")
    @JoinColumn(
            name = "org_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_org_activity_id")
    )
    @Setter
    private Organization organization;

    // 3. activity의 FK 선언
    // activity 1 : N hosted_by
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("activityId")
    @JoinColumn(
            name = "activity_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_activity_org_id")
    )
    private Activity activity;

    // 4. 기타 속성
    @Column(name="contribution")
    private Double contribution;    // 각 단체의 기여도 ex) 0.33, 0.5 등 소수점 표기 방식

    @Builder
    public HostedBy(HostedById hostedById,
                    Organization organization,
                    Activity activity,
                    Double contribution) {
        this.hostedById = hostedById;
        this.organization = organization;
        this.activity = activity;
        this.contribution = contribution;
    }
}
