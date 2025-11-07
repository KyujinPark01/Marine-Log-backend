package domain.backend.activity.dto.response;

import domain.backend.activity.domain.Activity;
import domain.backend.activity.domain.Status;
import domain.backend.organization.dto.HostedByResponse;
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
    /*후순위*/
    // todo LocationDto location,
    // todo List<HostedByResp> hostedBy

    public static ActivityResponse from(Activity a) {
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
                // ✅ 단체 정보 변환
                .hostedByList(
                        a.getHostedByList() == null ? null :
                                a.getHostedByList().stream()
                                        .map(HostedByResponse::from)
                                        .collect(Collectors.toList())
                )
                .build();
    }
    public static ActivityResponse fromWithCover(Activity a, String coverUrl) {
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
                .coverUrl(coverUrl)  // ✅ 대표사진 추가
                .build();
    }
}
