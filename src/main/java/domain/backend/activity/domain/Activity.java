package domain.backend.activity.domain;

import domain.backend.activity.dto.request.ActivityUpdateRequest;
import domain.backend.auth.domain.user.User;
import domain.backend.common.entity.BaseTimeEntity;
import domain.backend.common.entity.Waste;
import domain.backend.location.domain.Location;
import domain.backend.organization.domain.entity.HostedBy;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "activity")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    @OneToOne(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
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

    @Builder
    public Activity(User createdBy,
                    String name,
                    String description,
                    LocalDate activityDate,
                    LocalTime startTime,
                    LocalTime endTime,
                    Integer participants,
                    Status status,
                    Double weightKg,
                    Double volumeL) {
        this.createdBy = createdBy;     //작성자 누락으로 추가
        this.name = name;
        this.description = description;
        this.activityDate = activityDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.participants = participants;
        this.status = status;
        this.weightKg = weightKg;
        this.volumeL = volumeL;
    }@OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Waste> wastes = new ArrayList<>();
    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HostedBy> hostedByList = new ArrayList<>();

    // 게시글 수정 - 추가된 메소드
    public void updateField(ActivityUpdateRequest request) {
        this.name = request.getName() != null ? request.getName() : this.name;
        this.description = request.getDescription() != null ? request.getDescription() : this.description;
        this.activityDate = request.getActivityDate() != null ? request.getActivityDate() : this.activityDate;
        this.startTime = request.getStartTime() != null ? request.getStartTime() : this.startTime;
        this.endTime = request.getEndTime() != null ? request.getEndTime() : this.endTime;
        this.participants = request.getParticipants() != null ? request.getParticipants() : this.participants;
        this.weightKg = request.getWeightKg() != null ? request.getWeightKg() : this.weightKg;
        this.volumeL = request.getVolumeL() != null ? request.getVolumeL() : this.volumeL;
    }
    public void setLocation(Location location) {
        this.location = location;
    }


    public void publish() { this.status = Status.PUBLISHED; }
    public void softDelete() { this.status = Status.DELETED; }
}