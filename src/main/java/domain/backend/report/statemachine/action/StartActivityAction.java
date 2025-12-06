package domain.backend.report.statemachine.action;

import domain.backend.auth.domain.user.User;
import domain.backend.report.domain.entity.*;
import domain.backend.report.domain.enums.ActivityEvent;
import domain.backend.report.domain.enums.ActivityStatus;
import domain.backend.report.domain.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

/**
 * 모집 → 활동 시작
 */
@Component
@RequiredArgsConstructor
public class StartActivityAction implements Action<ActivityStatus, ActivityEvent> {

    private final ActivityReportRepository reportRepository;
    private final ActivityStatusHistoryRepository historyRepository;

    @Override
    public void execute(StateContext<ActivityStatus, ActivityEvent> ctx) {

        Long reportId = (Long) ctx.getMessageHeader("reportId");
        User user = (User) ctx.getMessageHeader("user");

        ActivityReport report = reportRepository.findById(reportId)
                .orElseThrow();

        historyRepository.save(
                ActivityStatusHistory.builder()
                        .activityReport(report)
                        .fromStatus(report.getStatus())
                        .toStatus(ActivityStatus.IN_PROGRESS)
                        .changedBy(user)
                        .reason("활동 시작")
                        .build()
        );

        report.setStatus(ActivityStatus.IN_PROGRESS);
    }
}
