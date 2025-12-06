package domain.backend.report.statemachine.action;

import domain.backend.auth.domain.user.User;
import domain.backend.report.domain.enums.ActivityEvent;
import domain.backend.report.domain.entity.ActivityReport;
import domain.backend.report.domain.enums.ActivityStatus;
import domain.backend.report.domain.entity.ActivityStatusHistory;
import domain.backend.report.domain.repository.ActivityReportRepository;
import domain.backend.report.domain.repository.ActivityStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

/**
 * 승인 이벤트 발생 시 실행되는 비즈니스 로직
 * - ActivityStatusHistory 기록
 * - ActivityReport 상태 변경
 * - 필요 시 통보 / 서브엔티티 생성 등 확장 가능
 */
@Component
@RequiredArgsConstructor
public class ApproveAction implements Action<ActivityStatus, ActivityEvent> {

    private final ActivityReportRepository reportRepository;
    private final ActivityStatusHistoryRepository historyRepository;

    @Override
    public void execute(StateContext<ActivityStatus, ActivityEvent> ctx) {

        // 이벤트 헤더에서 reportId, user 정보 추출
        Long reportId = (Long) ctx.getMessageHeader("reportId");
        User user = (User) ctx.getMessageHeader("user");

        ActivityReport report = reportRepository.findById(reportId)
                .orElseThrow();

        // 상태 변경 이력 기록
        historyRepository.save(
                ActivityStatusHistory.builder()
                        .activityReport(report)
                        .fromStatus(report.getStatus())
                        .toStatus(ActivityStatus.APPROVED)
                        .changedBy(user)
                        .reason("관리자가 승인함")
                        .build()
        );

        // 실제 ActivityReport 상태도 변경
        report.setStatus(ActivityStatus.APPROVED);
    }
}
