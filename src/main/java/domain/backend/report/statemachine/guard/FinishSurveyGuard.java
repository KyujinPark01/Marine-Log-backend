package domain.backend.report.statemachine.guard;

import domain.backend.report.domain.entity.ActivityReport;
import domain.backend.report.domain.enums.ActivityEvent;
import domain.backend.report.domain.enums.ActivityStatus;
import domain.backend.report.domain.repository.ActivityReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FinishSurveyGuard implements Guard<ActivityStatus, ActivityEvent> {

    private final ActivityReportRepository reportRepository;

    @Override
    public boolean evaluate(StateContext<ActivityStatus, ActivityEvent> context) {

        Long reportId = (Long) context.getMessageHeader("reportId");

        ActivityReport report = reportRepository.findById(reportId)
                .orElseThrow();

        return report.getStatus() == ActivityStatus.SURVEYING;
    }
}
