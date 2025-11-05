package domain.backend.report.dto;

import domain.backend.report.domain.ActivityReport;
import domain.backend.report.domain.ActivityStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ActivityReportResponse {
    private Long id;
    private Double latitude;
    private Double longitude;
    private ActivityStatus status;

    public static ActivityReportResponse from(ActivityReport report) {
        return ActivityReportResponse.builder()
                .id(report.getId())
                .latitude(report.getLatitude())
                .longitude(report.getLongitude())
                .status(report.getStatus())
                .build();
    }
}
