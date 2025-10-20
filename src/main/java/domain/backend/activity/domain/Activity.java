package domain.backend.activity.domain;

import domain.backend.common.entity.BaseTimeEntity;
import domain.backend.common.entity.waste.Waste;
import domain.backend.organization.domain.entity.HostedBy;
import domain.backend.map.domain.Location;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "activity")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Activity extends BaseTimeEntity {

    // 1. PK 선언
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long activityId;

    // 2. Mappings
    @OneToMany(
            mappedBy = "activity",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
            )
    private List<HostedBy> hostedBys;

    @OneToMany(
            mappedBy = "activity",
            fetch = FetchType.LAZY
    )
    private List<Waste> wastes;

    @OneToOne(
            mappedBy = "activity",
            cascade = CascadeType.ALL
    )
    private Location location;

    @OneToMany(fetch = FetchType.LAZY)
    private List<ActivityPicture> activityPictures;

    // 3. 기타 속성
    @Column(name="name")
    private String name;

    @Column(name="description")
    private String description;

    @Column(name="activity_date")
    private LocalDate activityDate;

    @Column(name="start_time")
    private LocalTime startTime;

    @Column(name="end_time")
    private LocalTime endTime;

    @Column(name="max_participants")
    private Integer maxParticipants;

    @Column(name="current_participants")
    private Integer currentParticipants;

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name="weight")
    private double weightKg;

    @Column(name="volume")
    private double volumeL;
}