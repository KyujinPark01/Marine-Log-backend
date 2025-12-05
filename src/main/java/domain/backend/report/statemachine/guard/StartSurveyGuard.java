package domain.backend.report.statemachine.guard;

import domain.backend.report.domain.entity.ActivityReport;
import domain.backend.report.domain.enums.ActivityEvent;
import domain.backend.report.domain.enums.ActivityStatus;
import domain.backend.report.domain.repository.ActivityReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartSurveyGuard implements Guard<ActivityStatus, ActivityEvent> {


     //답사 시작은 APPROVED 상태에서만 허용, REJECTED면 불가

    private final ActivityReportRepository reportRepository;

    @Override
    public boolean evaluate(StateContext<ActivityStatus, ActivityEvent> context) {

        Long reportId = (Long) context.getMessageHeader("reportId");

        ActivityReport report = reportRepository.findById(reportId)
                .orElseThrow();

        // ANSWER: APPROVED 상태에서만 답사 시작 가능
        return report.getStatus() == ActivityStatus.APPROVED;
    }
}
