package domain.backend.common.entity;

import com.fasterxml.jackson.databind.ser.Serializers;
import domain.backend.activity.domain.Activity;
import domain.backend.auth.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "waste")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Waste extends BaseTimeEntity{

    @Id
    private Long id;

    private String specificType;
    private String type;
    private String environment;
    private String pictureUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id")
    private Activity activity;
}