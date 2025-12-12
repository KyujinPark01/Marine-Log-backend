package domain.backend.report.statemachine.guard;

import domain.backend.auth.domain.user.User;
import domain.backend.report.domain.entity.Collaboration;
import domain.backend.report.domain.enums.ActivityEvent;
import domain.backend.report.domain.enums.ActivityStatus;
import domain.backend.report.domain.enums.CollaborationStatus;
import domain.backend.report.domain.repository.CollaborationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CollaborationApproveGuard implements Guard<ActivityStatus, ActivityEvent> {

    private final CollaborationRepository collaborationRepository;

    @Override
    public boolean evaluate(StateContext<ActivityStatus, ActivityEvent> context) {

        Long reportId = (Long) context.getMessageHeader("reportId");
        Long partnerOrgId = (Long) context.getMessageHeader("partnerOrgId");
        User actor = (User) context.getMessageHeader("user");

        Collaboration collab = collaborationRepository.findByReportAndPartner(reportId, partnerOrgId)
                .orElse(null);

        if (collab == null) return false;

        // 요청 상태여야 승인 가능
        if (collab.getStatus() != CollaborationStatus.REQUESTED) return false;

        // 승인자는 targetOrg HOST여야 함
        return actor.getOrganization().getOrgId().equals(partnerOrgId);
    }
}
