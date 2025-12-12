package domain.backend.report.dto.response;

import domain.backend.report.domain.entity.ActivityReport;
import domain.backend.report.domain.enums.ActivityStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ActivityReportDetailResponse(
        Long id,
        String title,
        String reportedReason,
        String reporterName,
        String reporterOccupation,
        ActivityStatus status,
        String photoUrl,
        Double latitude,
        Double longitude,
        LocalDateTime reportedDate
) {
    public static ActivityReportDetailResponse from(ActivityReport report) {
        return ActivityReportDetailResponse.builder()
                .id(report.getActivityReportId())
                .title(report.getTitle())
                .reportedReason(report.getReportedReason())
                .reporterName(report.getReporter().getName()) // User 엔티티에서 이름 또는 닉네임 가져오기
                .reporterOccupation(report.getReporterOccupation())
                .status(report.getStatus())
                .photoUrl(report.getPhotoUrl())
                .latitude(report.getLatitude())
                .longitude(report.getLongitude())
                .reportedDate(report.getCreatedAt())
                .build();
    }
}