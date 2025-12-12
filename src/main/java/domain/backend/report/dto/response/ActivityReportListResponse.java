package domain.backend.report.dto.response;

import domain.backend.report.domain.entity.ActivityReport;
import domain.backend.report.domain.enums.ActivityStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ActivityReportListResponse(
        Long id,
        String title,
        String location, // 위치 정보 (예: 위경도 또는 주소)
        LocalDateTime reportedDate, // 제보일 (BaseTimeEntity의 created_at 사용)
        ActivityStatus status
) {
    public static ActivityReportListResponse from(ActivityReport report) {
        // 위치를 적절한 문자열로 변환 (지오메트리 객체 대신 주소 또는 간단한 좌표 정보)
        String locationString = (report.getLatitude() != null && report.getLongitude() != null)
                ? String.format("%.4f, %.4f", report.getLatitude(), report.getLongitude())
                : "위치 정보 없음";

        // 사용자 편의를 위해 'reportedDate'는 BaseTimeEntity의 생성 시간을 사용
        return ActivityReportListResponse.builder()
                .id(report.getActivityReportId())
                .title(report.getTitle())
                .location(locationString) // 실제 주소를 사용하려면, 엔티티에 주소 필드가 있어야 함. 여기서는 좌표로 대체
                .reportedDate(report.getCreatedAt())
                .status(report.getStatus())
                .build();
    }
}