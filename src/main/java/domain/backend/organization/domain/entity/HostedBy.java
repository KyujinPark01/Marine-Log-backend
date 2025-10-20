package domain.backend.organization.domain.entity;

import domain.backend.activity.domain.Activity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

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
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HostedBy {

    // 1. PK 선언
    @Id
    @Column(name = "attend_org_id")
    private Long attendOrgId;

    // 2. organization의 FK 선언
    // activity 1 : N waste
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "org_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_org_activity_id")
    )
    private Organization orgActivityId;

    // 3. activity의 FK 선언
    // activity 1 : N hosted_by
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "activity_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_activity_org_id")
    )
    private Activity activityOrgId;

    // 4. 기타 속성
    @Column(name="contribution")
    private BigDecimal contribution;    // 각 단체의 기여도 ex) 0.33, 0.5 등 소수점 표기 방식
}
