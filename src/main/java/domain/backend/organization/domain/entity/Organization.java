package domain.backend.organization.domain.entity;

import domain.backend.activity.domain.Activity;
import domain.backend.auth.domain.user.User;
import domain.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "organization")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Organization extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "org_id")
    private Long orgId;

    @Column(name="name")
    private String name;

    @Column(name="location")
    private String location;

    @Column(name="description")
    private String description;

    @Builder
    public Organization(String name, String location, String description) {
        this.name = name;
        this.location = location;
        this.description = description;
    }

}