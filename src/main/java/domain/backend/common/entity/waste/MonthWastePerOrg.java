package domain.backend.common.entity.waste;

import domain.backend.organization.domain.entity.Organization;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/*
 * 단체별 월별 폐기물 수거량 엔티티
 */

@Entity
@Table(
        name = "monthly_waste_per_org",
        uniqueConstraints = {
                // 한 단체당 {연도, 월}별 하나의 총 폐기물 수거량만 있으므로 유니크 키로 선언
                @UniqueConstraint(
                        name="uk_waste_org_id",
                        columnNames={"org_id", "year", "month"}
                )
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MonthWastePerOrg {

    // 1. PK 선언
    @Id
    @Column(name="waste_org_id")
    private Long wasteOrgId;

    // 2. org_id의 PFK 선언
    // organization 1 : N monthly_waste_per_org
    @ManyToOne
    @JoinColumn(name="org_id")
    private Organization orgId;

    // 3. 기타 속성
    @Column(
            name = "year",
            nullable = false
    )
    private Integer year;

    @Column(
            name = "month",
            nullable = false
    )
    private Integer month;

    @Column(name = "weight_kg",
            precision = 10,
            scale = 2)
    private BigDecimal weightKg;

    @Column(name = "volume_L",
            precision = 10,
            scale = 2)
    private BigDecimal volumeL;
}
