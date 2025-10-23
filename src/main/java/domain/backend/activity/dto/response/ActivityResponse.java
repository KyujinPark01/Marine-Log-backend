package domain.backend.activity.dto.response;

import domain.backend.activity.domain.Activity;
import domain.backend.activity.domain.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
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
    private Status status;        // "DRAFT" | "PUBLISHED" | "DELETED"
    private String createdByName;
    /*LocationDto location,
        List<PictureResp> pictures,
        List<HostedByResp> hostedBy, - 후순위 */

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
                .build();
    }
}
