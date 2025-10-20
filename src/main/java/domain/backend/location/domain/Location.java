package domain.backend.location.domain;

import domain.backend.activity.domain.Activity;
import domain.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

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

    @Column(columnDefinition = "geometry(Point, 4326)") // PostGIS 전용 geometry 데이터타입
    private Point geometry;
    private String region1depthName;
    private String region2depthName;
    private String region3depthName;
    private String address;

    @OneToOne
    @MapsId
    @JoinColumn(name = "activity_id")
    private Activity activity;
}
