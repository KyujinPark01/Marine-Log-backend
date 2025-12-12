package domain.backend.report.statemachine.action;

import domain.backend.report.domain.enums.ActivityEvent;
import domain.backend.report.domain.enums.ActivityStatus;
import domain.backend.report.service.CollaborationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CollaborationRejectAction implements Action<ActivityStatus, ActivityEvent> {

    private final CollaborationService collaborationService;

    @Override
    public void execute(StateContext<ActivityStatus, ActivityEvent> context) {
        Long reportId = (Long) context.getMessageHeader("reportId");
        Long partnerOrgId = (Long) context.getMessageHeader("partnerOrgId");

        collaborationService.rejectRequest(reportId, partnerOrgId);

        log.info("❌ 연합 반려 완료: reportId={}, partnerOrgId={}", reportId, partnerOrgId);
    }
}