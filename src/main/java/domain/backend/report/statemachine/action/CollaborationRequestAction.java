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
public class CollaborationRequestAction implements Action<ActivityStatus, ActivityEvent> {

    private final CollaborationService collaborationService;

    @Override
    public void execute(StateContext<ActivityStatus, ActivityEvent> context) {
        // 헤더에서 ID 추출
        Long reportId = (Long) context.getMessageHeader("reportId");
        Long partnerOrgId = (Long) context.getMessageHeader("partnerOrgId");

        if (reportId == null || partnerOrgId == null) {
            log.error("❌ 연합 요청 실패: ID 정보 누락");
            return;
        }

        // Service 호출 (이제 에러가 사라집니다)
        collaborationService.createRequest(reportId, partnerOrgId);

        log.info("✅ 연합 활동 요청 생성됨: reportId={}, partnerOrgId={}", reportId, partnerOrgId);
    }
}