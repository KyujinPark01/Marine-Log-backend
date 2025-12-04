package domain.backend.activity.dto.response;

import domain.backend.activity.domain.Activity;
import domain.backend.activity.domain.Status;
import domain.backend.organization.dto.response.HostedByResponse;
import domain.backend.waste.dto.response.WasteResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
public class ActivityResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDate activityDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer participants;
    private Status status;
    private String createdByName;
    private String coverUrl;

    // ✅ 추가
    private List<HostedByResponse> hostedByList;

    private Double lat;  // 위도
    private Double lng;  // 경도
    private List<WasteResponse> wastes;  // 폐기물 목록
    private List<ActivityPictureResponse> pictures;

    public static ActivityResponse from(Activity a, String coverUrl) {
        return ActivityResponse.builder()
                .id(a.getActivityId())
                .name(a.getName())
                .description(a.getDescription())
                .activityDate(a.getActivityDate())
                .startTime(a.getStartTime())
                .endTime(a.getEndTime())
                .participants(a.getParticipants())
                .status(a.getStatus())
                .createdByName(a.getCreatedBy().getName())
                .coverUrl(coverUrl)
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
                //todo : pictures 도 가져와야함
                .build();
    }
}
