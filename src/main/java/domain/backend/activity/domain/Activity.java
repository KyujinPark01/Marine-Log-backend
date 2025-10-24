package domain.backend.activity.domain;

import domain.backend.auth.domain.user.User;
import domain.backend.common.entity.BaseTimeEntity;
import domain.backend.location.domain.Location;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "activity")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Activity extends BaseTimeEntity {

    // 1. PK 선언
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long activityId;

    // 2. 작성자 (User와 다대일 관계) - 활동 데이터 입력시 작성자 필수라고 생각하여 추가.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_activity_user_id")
    )
    private User createdBy;

    // 2. Mappings
    @OneToOne(
            mappedBy = "activity",
            cascade = CascadeType.ALL
    )
    private Location location;

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

    @Column(name="participants")
    private Integer participants;

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name="weight_kg")
    private Double weightKg;

    @Column(name="volume_L")
    private Double volumeL;
}