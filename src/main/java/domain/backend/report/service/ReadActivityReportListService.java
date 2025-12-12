package domain.backend.report.service;

import domain.backend.report.domain.entity.ActivityReport;
import domain.backend.report.domain.enums.ActivityStatus;
import domain.backend.report.domain.repository.ActivityReportRepository;
import domain.backend.report.dto.response.ActivityReportDetailResponse;
import domain.backend.report.dto.response.ActivityReportListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReadActivityReportListService {

    private final ActivityReportRepository activityReportRepository;

    /**
     * 모든 활동 보고서를 페이징하여 목록 조회 DTO로 반환합니다.
     * @param pageable 페이징 정보 (페이지 번호, 페이지 크기, 정렬)
     * @return 활동 보고서 목록이 담긴 페이지
     */
    public Page<ActivityReportListResponse> getPagedActivityReports(ActivityStatus status, Pageable pageable) {
        Page<ActivityReport> reports;

        if (status != null) {
            // ✅ 상태가 있으면 해당 상태만 조회
            reports = activityReportRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        } else {
            // ❌ 상태가 없으면(null) 전체 조회
            reports = activityReportRepository.findAllByOrderByCreatedAtDesc(pageable);
        }

        return reports.map(ActivityReportListResponse::from);
    }

    /**
     * 특정 활동 보고서의 상세 정보를 조회합니다.
     * @param reportId 활동 보고서 ID
     * @return 상세 조회 DTO
     */
    public ActivityReportDetailResponse getActivityReportDetail(Long reportId) {
        ActivityReport report = activityReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("ActivityReport not found with id: " + reportId));

        return ActivityReportDetailResponse.from(report);
    }
}