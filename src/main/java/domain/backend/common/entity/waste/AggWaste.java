package domain.backend.common.entity.waste;

import domain.backend.activity.domain.Activity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/*
 * 폐기물 총 수거량 엔티티 (폐기물 총 수거량과 1:1 대응)
 */

@Entity
@Table(name = "agg_waste")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AggWaste {

    // 1. PK 선언
    @Id
    @Column(name = "agg_waste_id")
    private Long id;

    // 2. agg_waste의 PFK 선언
    // organization 1 : 1 agg_waste
    @OneToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @MapsId
    @JoinColumn(
            name="activity_id",
            foreignKey = @ForeignKey(name = "fk_aggwaste_activity")
    )
    private Activity activity;

    // 3. 기타 속성 선언
    @Column(name = "weight_kg", precision = 10, scale = 2)
    private BigDecimal weight_kg;

    @Column(name = "volume_L")
    private BigDecimal volume_L;
}