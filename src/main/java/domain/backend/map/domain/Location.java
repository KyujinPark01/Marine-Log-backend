package domain.backend.map.domain;

import domain.backend.activity.domain.Activity;
import domain.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "location")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location extends BaseTimeEntity{

    @Id
    private Long activityId;

    private String geometry;
    private String region1depthName;
    private String region2depthName;
    private String region3depthName;
    private String address;

    @OneToOne
    @MapsId
    @JoinColumn(name = "activity_id")
    private Activity activity;
}
