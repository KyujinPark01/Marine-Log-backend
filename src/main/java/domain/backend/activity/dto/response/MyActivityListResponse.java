package domain.backend.activity.dto.response;

import domain.backend.activity.domain.Activity;
import domain.backend.activity.domain.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/*
 * 내 활동 목록 조회용 Response
 * - 테이블형 UI
 * - 관리/수정 목적
 */
@Getter
@Builder
@AllArgsConstructor
public class MyActivityListResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDate activityDate;
    private String location;
    private Status status;
    private LocalDateTime updatedAt;

    public static MyActivityListResponse from(Activity a) {

        return MyActivityListResponse.builder()
                .id(a.getActivityId())
                .name(a.getName())
                .activityDate(a.getActivityDate())
                .description(a.getDescription())
                .status(a.getStatus())
                .updatedAt(a.getUpdatedAt())
                .build();
    }
}
