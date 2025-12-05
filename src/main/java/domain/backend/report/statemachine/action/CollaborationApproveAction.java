package domain.backend.report.statemachine.action;

import domain.backend.report.domain.entity.Collaboration;
import domain.backend.report.domain.enums.ActivityEvent;
import domain.backend.report.domain.enums.ActivityStatus;
import domain.backend.report.domain.enums.CollaborationStatus;
import domain.backend.report.domain.repository.CollaborationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CollaborationApproveAction implements Action<ActivityStatus, ActivityEvent> {

    private final CollaborationRepository collaborationRepository;

    @Override
    public void execute(StateContext<ActivityStatus, ActivityEvent> context) {

        Long reportId = (Long) context.getMessageHeader("reportId");
        Long partnerOrgId = (Long) context.getMessageHeader("partnerOrgId");

        Collaboration collab =
                collaborationRepository.findByActivityReport_ActivityReportIdAndTargetOrg_OrgId(reportId, partnerOrgId)
                        .orElseThrow();

        collab.approve();
        collaborationRepository.save(collab);

        log.info("🔥 Collaboration approved for reportId={}, partnerOrgId={}", reportId, partnerOrgId);
    }
}
