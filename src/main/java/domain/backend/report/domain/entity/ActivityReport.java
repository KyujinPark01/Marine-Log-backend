package domain.backend.report.domain.entity;

import domain.backend.auth.domain.user.User;
import domain.backend.common.entity.BaseTimeEntity;
import domain.backend.report.domain.enums.ActivityStatus;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "activity_report")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ActivityReport extends BaseTimeEntity {

    @Id
    @Column(name = "activity_report_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long activityReportId;

    // user 와 1 대 N으로 mapping되어 있음
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reporter_user_id")
    private User reporter;

    @Column(length = 200)
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
    @OneToMany(mappedBy = "activityReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ActivityStatusHistory> historyList = new ArrayList<>();
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

    public Double getLongitude() {
        return this.location != null ? this.location.getX() : null;
    }

    public Double getLatitude() {
        return this.location != null ? this.location.getY() : null;
    }

    public void setStatus(ActivityStatus status) {
        this.status = status;
    }
}
