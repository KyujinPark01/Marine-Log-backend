package domain.backend.activity.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "activity_picture")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ActivityPicture {
    // 1. PK 생성
    @Id
    @Column(name = "picture_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pictureId;

    // 2. activity_id의 FK 선언
    // activity 1 : N waste
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "activity_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_activity_picture_id")
    )
    private Activity activity;

    // 3. 기타 속성들 선언
    @Column(
            name = "picture_url",
            nullable = false
    )
    private String pictureUrl;

    @Column(name = "summary")
    private String summary;

    @Builder
    public ActivityPicture(Activity activity, String pictureUrl, String summary) {
        this.activity = activity;
        this.pictureUrl = pictureUrl;
        this.summary = summary;
    }
}
