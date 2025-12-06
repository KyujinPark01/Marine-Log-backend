package domain.backend.activity.dto.response;

import domain.backend.activity.domain.Activity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.stream.Collectors;

/*
 * 공개 활동 목록 조회용 Response
 * - 썸네일 카드형 UI
 * - 일반 사용자가 보는 목록
 */
@Getter
@Builder
@AllArgsConstructor
public class PublicActivityListResponse {
    private Long id;
    private String name;
    private String description;
    private String coverUrl;
    private LocalDate activityDate;
    private String location;
    private String organizationNames;

    public static PublicActivityListResponse from(Activity a, String coverUrl) {
        String orgNames = (a.getHostedByList() == null || a.getHostedByList().isEmpty())
                ? null
                : a.getHostedByList().stream()
                .map(hostedBy -> hostedBy.getOrganization().getName())
                .collect(Collectors.joining(", "));

        return PublicActivityListResponse.builder()
                .id(a.getActivityId())
                .name(a.getName())
                .coverUrl(coverUrl)
                .activityDate(a.getActivityDate())
                .description(a.getDescription())
                .organizationNames(orgNames)
                .build();
    }
}