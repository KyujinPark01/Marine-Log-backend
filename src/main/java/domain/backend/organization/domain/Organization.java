package domain.backend.organization.domain;

import com.fasterxml.jackson.databind.ser.Serializers;
import domain.backend.activity.domain.Activity;
import domain.backend.auth.domain.user.User;
import domain.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "organization")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organization extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String location;
    private String description;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL)
    private List<User> users;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL)
    private List<Activity> activities;
}