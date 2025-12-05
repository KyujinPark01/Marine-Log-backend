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
 * 답사 → 모집
 */
@Component
@RequiredArgsConstructor
public class FinishSurveyAction implements Action<ActivityStatus, ActivityEvent> {

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
                        .toStatus(ActivityStatus.RECRUITING)
                        .changedBy(user)
                        .reason("답사 완료 → 모집 단계로 전환")
                        .build()
        );

        report.setStatus(ActivityStatus.RECRUITING);
    }
}
