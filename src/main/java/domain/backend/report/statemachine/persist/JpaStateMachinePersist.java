package domain.backend.report.statemachine.persist;

import domain.backend.report.domain.entity.ActivityReport;
import domain.backend.report.domain.enums.ActivityEvent;
import domain.backend.report.domain.enums.ActivityStatus;
import domain.backend.report.domain.repository.ActivityReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional; // 트랜잭션 추가 권장

@Component
@RequiredArgsConstructor
public class JpaStateMachinePersist
        implements StateMachinePersist<ActivityStatus, ActivityEvent, Long> {

    private final ActivityReportRepository reportRepository;

    @Override
    @Transactional // 쓰기 작업이므로 트랜잭션 적용 권장
    public void write(StateMachineContext<ActivityStatus, ActivityEvent> context, Long reportId) {
        ActivityReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found")); // 예외 처리 명확히

        // 1. 메모리 상의 객체 상태 변경
        report.setStatus(context.getState());

        // 2. 🔥 [중요] 변경된 내용을 DB에 저장 (이 부분이 없어서 DB가 안 바뀜)
        reportRepository.save(report);
    }

    @Override
    @Transactional(readOnly = true) // 읽기 전용 트랜잭션
    public StateMachineContext<ActivityStatus, ActivityEvent> read(Long reportId) {
        ActivityReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));

        return new DefaultStateMachineContext<>(
                report.getStatus(),
                null,
                null,
                null
        );
    }
}