package domain.backend.activity.domain;

import domain.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "activity_review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityReview extends BaseTimeEntity {
    @Id
    private Long id;

    private String text;
    private String pictureUrl;


    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private ActivityAttend attend;
}
