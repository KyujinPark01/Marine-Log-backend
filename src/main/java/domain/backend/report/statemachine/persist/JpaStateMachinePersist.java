package domain.backend.report.statemachine.persist;

import domain.backend.report.domain.entity.*;
import domain.backend.report.domain.enums.ActivityEvent;
import domain.backend.report.domain.enums.ActivityStatus;
import domain.backend.report.domain.repository.ActivityReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.stereotype.Component;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.support.DefaultStateMachineContext;

/**
 * StateMachine의 현재 상태를 DB(ActivityReport.status)에 저장/로드
 */
@Component
@RequiredArgsConstructor
public class JpaStateMachinePersist
        implements StateMachinePersist<ActivityStatus, ActivityEvent, Long> {

    private final ActivityReportRepository reportRepository;

    @Override
    public void write(StateMachineContext<ActivityStatus, ActivityEvent> context, Long reportId) {
        ActivityReport report = reportRepository.findById(reportId)
                .orElseThrow();
        report.setStatus(context.getState());
    }

    @Override
    public StateMachineContext<ActivityStatus, ActivityEvent> read(Long reportId) {

        ActivityReport report = reportRepository.findById(reportId)
                .orElseThrow();

        return new DefaultStateMachineContext<>(
                report.getStatus(),
                null,
                null,
                null
        );
    }
}
