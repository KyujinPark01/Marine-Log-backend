package domain.backend.common.entity;

import domain.backend.activity.domain.Activity;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Waste {
    // 1. PK 생성
    @Id
    @Column(name = "waste_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wasteId;

    // 2. activity_id의 PFK 선언
    // activity 1 : N waste
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "activity_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_waste_activity_id")
    )
    private Activity activity;

    // 3. 기타 속성들 선언
    @Column(name = "specific_type")
    private String specificType;

    @Column(name = "type")
    private String type;

    @Column(name = "picture_url")
    private String pictureUrl;

    @Column(name = "weight_kg")
    private Double weightKg;

    @Column(name = "volume_L")
    private Double volumeL;

    @Builder
    public Waste(Activity activity,
                 String specificType,
                 String type,
                 String pictureUrl,
                 Double weightKg,
                 Double volumeL) {
        this.activity = activity;
        this.specificType = specificType;
        this.type = type;
        this.pictureUrl = pictureUrl;
        this.weightKg = weightKg;
        this.volumeL = volumeL;
    }
}