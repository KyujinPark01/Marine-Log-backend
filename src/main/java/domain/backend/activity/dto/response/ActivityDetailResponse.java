package domain.backend.activity.dto.response;

import domain.backend.activity.domain.Activity;
import domain.backend.activity.domain.ActivityPicture;
import domain.backend.activity.domain.Status;
import domain.backend.organization.dto.response.HostedByResponse;
import domain.backend.waste.dto.response.WasteResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@Builder
@AllArgsConstructor
public class ActivityDetailResponse {
    //기본 정보
    private Long id;
    private String name;
    private String description;
    //일시 정보
    private LocalDate activityDate;
    private LocalTime startTime;
    private LocalTime endTime;
    //참여/수거 정보
    private Integer participants;
    private Double weightKg;
    private Double volumeL;
    //상세 정보
    private Status status;
    private String createdByName;
    private String coverUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    //위치 정보
    private Double lat;
    private Double lng;
    //관계 정보
    private List<HostedByResponse> hostedByList;
    private List<WasteResponse> wastes;
    private List<ActivityPictureResponse> pictures;  // 모든 사진

    public static ActivityDetailResponse from(Activity a, List<ActivityPicture> pictures) {
        // coverUrl은 pictures의 첫 번째 사진
        String coverUrl = (pictures == null || pictures.isEmpty())
                ? null
                : pictures.get(0).getPictureUrl();

        return ActivityDetailResponse.builder()
                .id(a.getActivityId())
                .name(a.getName())
                .description(a.getDescription())
                .activityDate(a.getActivityDate())
                .startTime(a.getStartTime())
                .endTime(a.getEndTime())
                .participants(a.getParticipants())
                .weightKg(a.getWeightKg())
                .volumeL(a.getVolumeL())
                .status(a.getStatus())
                .createdByName(a.getCreatedBy().getName())
                .coverUrl(coverUrl)
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .hostedByList(
                        a.getHostedByList() == null ? null :
                                a.getHostedByList().stream()
                                        .map(HostedByResponse::from)
                                        .collect(Collectors.toList())
                )
                .wastes(
                        a.getWastes() == null ? null :
                                a.getWastes().stream()
                                        .map(WasteResponse::from)
                                        .collect(Collectors.toList())
                )
                .pictures(
                        pictures == null || pictures.isEmpty() ? null :
                                pictures.stream()
                                        .map(ActivityPictureResponse::from)
                                        .collect(Collectors.toList())
                )
                .build();
    }
}