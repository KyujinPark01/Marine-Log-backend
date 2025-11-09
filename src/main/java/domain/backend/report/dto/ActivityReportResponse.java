package domain.backend.report.dto;

import domain.backend.report.domain.ActivityReport;
import domain.backend.report.domain.ActivityStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ActivityReportResponse {
    private Long id;
    private String title;
    private String reportedReason;
    private String reporterOccupation;
    private String photoUrl;
    private Double latitude;
    private Double longitude;
    private ActivityStatus status;

    public static ActivityReportResponse from(ActivityReport report) {
        return ActivityReportResponse.builder()
                .id(report.getActivityReportId())
                .title(report.getTitle())
                .reportedReason(report.getReportedReason())
                .reporterOccupation(report.getReporterOccupation())
                .photoUrl(report.getPhotoUrl())
                .latitude(report.getLatitude())
                .longitude(report.getLongitude())
                .status(report.getStatus())
                .build();
    }
}
