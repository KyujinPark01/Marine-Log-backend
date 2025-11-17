package domain.backend.location.domain;

import domain.backend.activity.domain.Activity;
import domain.backend.activity.repository.ActivityPictureRepository;
import domain.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

@Entity
@Table(name = "location")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location extends BaseTimeEntity {
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

    @Builder
    public Location(
                    Activity activity,
                    Double latitude,
                    Double longitude,
                    String region1depthName,
                    String region2depthName,
                    String region3depthName,
                    String address) {
        if (latitude != null && longitude != null) {
            GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);
            this.geometry = factory.createPoint(new Coordinate(longitude, latitude));
        }

        this.activity = activity;
        this.region1depthName = region1depthName;
        this.region2depthName = region2depthName;
        this.region3depthName = region3depthName;
        this.address = address;
    }
}
