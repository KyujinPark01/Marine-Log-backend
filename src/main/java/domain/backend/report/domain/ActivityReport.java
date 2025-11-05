package domain.backend.report.domain;

import domain.backend.auth.domain.user.User;
import domain.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

@Entity
@Table(name = "activity_report")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ActivityReport extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reporter_user_id")
    private User reporter;

    @Column(length = 200, nullable = false)
    private String title;

    @Column(length = 500)
    private String reportedReason;

    @Column(length = 100)
    private String reporterOccupation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityStatus status;

    @Column(nullable = false)
    private String photoUrl;

    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point location;

    @Builder
    public ActivityReport(User reporter, String title, String reportedReason, String reporterOccupation,
                          ActivityStatus status, String photoUrl, Double latitude, Double longitude) {
        this.reporter = reporter;
        this.title = title;
        this.reportedReason = reportedReason;
        this.reporterOccupation = reporterOccupation;
        this.status = status;
        this.photoUrl = photoUrl;
        if (latitude != null && longitude != null) {
            GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);
            this.location = factory.createPoint(new Coordinate(longitude, latitude));
        }
    }

    public Double getLatitude() {
        return this.location != null ? this.location.getY() : null;
    }

    public Double getLongitude() {
        return this.location != null ? this.location.getX() : null;
    }
}
