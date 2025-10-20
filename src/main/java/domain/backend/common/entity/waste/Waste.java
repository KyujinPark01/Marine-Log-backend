package domain.backend.common.entity.waste;

import domain.backend.activity.domain.Activity;
import domain.backend.auth.domain.user.User;
import domain.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Waste {

    // 1. PK 생성
    @Id
    @Column(name = "waste_id")
    private Long wasteId;

    // 2. activity_id의 PFK 선언
    // activity 1 : N waste
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("activityId")       // 괄호 안에는 activity table의 DB상 PK 명이 아니라 식별자 클래스의 이름을 기입
    @JoinColumn(
            name = "activity_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_waste_activity_id"))
    private Activity activityId;

    // 3. 기타 속성들 선언
    @Column(name = "specific_type")
    private String specificType;

    @Column(name = "type")
    private String type;

    @Column(name = "picture_url")
    private String pictureUrl;

    @Column(name = "weight_kg", precision = 10, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "volume_L", precision = 10, scale = 2)
    private BigDecimal volumeL;

}