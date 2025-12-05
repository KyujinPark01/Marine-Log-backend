package domain.backend.report.statemachine.action;

import domain.backend.report.domain.entity.ActivityReport;
import domain.backend.report.domain.enums.ActivityEvent;
import domain.backend.report.domain.enums.ActivityStatus;
import domain.backend.report.domain.repository.ActivityReportRepository;
import domain.backend.report.service.CollaborationService;
import domain.backend.report.statemachine.util.StateMachineHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CollaborationRequestAction implements Action<ActivityStatus, ActivityEvent> {

    private final CollaborationService collaborationService; // 연합 요청 저장 서비스
    private final ActivityReportRepository reportRepository;

    @Override
    public void execute(StateContext<ActivityStatus, ActivityEvent> context) {

        Long reportId = StateMachineHelper.getReportId(context);
        ActivityReport report = reportRepository.findById(reportId).orElseThrow();

        Long partnerOrgId = (Long) context.getMessageHeader("partnerOrgId");

        collaborationService.createRequest(report, partnerOrgId);

        log.info("연합 활동 요청 생성됨: report={}, partnerOrg={}", reportId, partnerOrgId);
    }
}
