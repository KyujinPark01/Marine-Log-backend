package domain.backend.report.statemachine.action;

import domain.backend.activity.domain.Activity;
import domain.backend.activity.domain.Status;
import domain.backend.activity.repository.ActivityRepository;
import domain.backend.auth.domain.user.User;
import domain.backend.location.service.LocationService;
import domain.backend.report.domain.entity.*;
import domain.backend.report.domain.enums.ActivityEvent;
import domain.backend.report.domain.enums.ActivityStatus;
import domain.backend.report.domain.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

/**
 * 답사 → 모집
 */
@Component
@RequiredArgsConstructor
public class FinishSurveyAction implements Action<ActivityStatus, ActivityEvent> {

    private final ActivityReportRepository reportRepository;
    private final ActivityStatusHistoryRepository historyRepository;
    private final LocationService locationService;
    private final ActivityRepository activityRepository;

    @Override
    @Transactional
    public void execute(StateContext<ActivityStatus, ActivityEvent> ctx) {

        Long reportId = (Long) ctx.getMessageHeader("reportId");
        User user = (User) ctx.getMessageHeader("user");

        ActivityReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));

        // 1) 상태 히스토리 기록
        historyRepository.save(
                ActivityStatusHistory.builder()
                        .activityReport(report)
                        .fromStatus(report.getStatus())
                        .toStatus(ActivityStatus.RECRUITING)
                        .changedBy(user)
                        .reason("답사 완료 → 모집 단계로 전환")
                        .build()
        );

        // 2) 보고서 상태 변경
        report.setStatus(ActivityStatus.RECRUITING);

        // 3) 임시 Activity 생성
        Activity activity = Activity.builder()
                .createdBy(report.getReporter())
                .name(report.getTitle())
                .description(report.getReportedReason())
                .status(Status.DRAFT)
                .build();

        activityRepository.save(activity);

        if (report.getLatitude() != null && report.getLongitude() != null) {
            locationService.saveForActivity(activity, report.getLatitude(), report.getLongitude());
        }
    }
}
