package domain.backend.activity.dto.response;

import domain.backend.activity.domain.ActivityPicture;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ActivityPictureResponse {

    private Long pictureId;     // 사진 ID
    private String pictureUrl;  // 사진 URL
    private String summary;     // 요약 설명

    public static ActivityPictureResponse from(ActivityPicture picture) {
        return ActivityPictureResponse.builder()
                .pictureId(picture.getPictureId())
                .pictureUrl(picture.getPictureUrl())
                .summary(picture.getSummary())
                .build();
    }
}
