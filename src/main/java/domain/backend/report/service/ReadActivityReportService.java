package domain.backend.report.service;

import domain.backend.report.domain.entity.ActivityReport;
import domain.backend.report.domain.enums.ActivityStatus;
import domain.backend.report.dto.response.ActivityReportResponse;
import domain.backend.report.domain.repository.ActivityReportRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReadActivityReportService {
    private final ActivityReportRepository activityReportRepository;

    public List<ActivityReportResponse> getAllReportsForMap() {
        List<ActivityStatus> statuses = List.of(ActivityStatus.APPROVED, ActivityStatus.SURVEYING, ActivityStatus.RECRUITING);
        return activityReportRepository.findByStatusIn(statuses).stream()
                .map(ActivityReportResponse::from)
                .collect(Collectors.toList());
    }

    public ActivityReportResponse getSingleReport(Long activityReportId) {
        ActivityReport report = activityReportRepository.findById(activityReportId)
                .orElseThrow(() -> new IllegalArgumentException("ActivityReport not found with id: " + activityReportId));
        return ActivityReportResponse.from(report);
    }
}
