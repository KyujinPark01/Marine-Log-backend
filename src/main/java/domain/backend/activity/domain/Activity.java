package domain.backend.activity.domain;

import domain.backend.common.entity.BaseTimeEntity;
import domain.backend.common.entity.Waste;
import domain.backend.map.domain.Geography;
import domain.backend.notification.domain.Notification;
import domain.backend.organization.domain.Organization;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "activity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private LocalDate startDatetime;
    private LocalDate endDatetime;
    private Integer maxParticipants;
    private Integer currentParticipants;

    @Enumerated(EnumType.STRING)
    private Status status;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL)
    private List<Waste> wastes;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL)
    private List<ActivityAttend> attends;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL)
    private List<Notification> notifications;

    @OneToOne(mappedBy = "activity", cascade = CascadeType.ALL)
    private Geography geography;

}