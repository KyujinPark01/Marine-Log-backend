package domain.backend.map.domain;

import domain.backend.activity.domain.Activity;
import domain.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "geography")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Geography extends BaseTimeEntity{

    @Id
    private Integer activityId;

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
