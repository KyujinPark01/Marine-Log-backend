package domain.backend.organization.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable             // 여러 값의 타입을 합쳐서 하나의 타입으로 새로운 값 타입으로 정의하여 사용
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@ToString               // Composite Key 구분을 위해 equals, hashCode, toString이 구현되어야 함
public class HostedById {
    @Column(
            name = "activity_id",
            nullable = false
    )
    private Long activityId;

    @Column(
            name = "org_id",
            nullable = false
    )
    private Long orgId;

    @Builder
    public HostedById(Long activityId,
                      Long orgId) {
        this.activityId = activityId;
        this.orgId = orgId;
    }
}
