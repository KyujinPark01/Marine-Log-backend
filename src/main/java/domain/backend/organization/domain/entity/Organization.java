package domain.backend.organization.domain.entity;

import domain.backend.activity.domain.Activity;
import domain.backend.auth.domain.user.User;
import domain.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "organization")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Organization extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(
            mappedBy = "organization",
            cascade = CascadeType.ALL
    )
    private List<User> users;

    @OneToMany(
            mappedBy = "orgActivityId",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    private List<HostedBy> hostedBys;

    @Column(name="name")
    private String name;

    @Column(name="location")
    private String location;

    @Column(name="description")
    private String description;
}