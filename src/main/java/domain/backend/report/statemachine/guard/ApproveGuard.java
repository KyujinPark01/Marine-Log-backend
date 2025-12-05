package domain.backend.report.statemachine.guard;

import domain.backend.auth.domain.user.Role;
import domain.backend.auth.domain.user.User;
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
public class ApproveGuard implements Guard<ActivityStatus, ActivityEvent> {

    private final ActivityReportRepository reportRepository;

    @Override
    public boolean evaluate(StateContext<ActivityStatus, ActivityEvent> context) {

        Long reportId = (Long) context.getMessageHeader("reportId");
        User actor = (User) context.getMessageHeader("user");

        ActivityReport report = reportRepository.findById(reportId)
                .orElseThrow();

        // REPORTED 상태에서만 승인 가능 + HOST 권한 필요
        return report.getStatus() == ActivityStatus.REPORTED
                && actor != null
                && actor.getRole() == Role.HOST;
    }
}

